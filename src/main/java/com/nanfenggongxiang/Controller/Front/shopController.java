package com.nanfenggongxiang.Controller.Front;

import com.nanfenggongxiang.Dao.CommodityDao;
import com.nanfenggongxiang.Dao.CommodityMapper;
import com.nanfenggongxiang.Dao.countDao;
import com.nanfenggongxiang.Domain.Commodity;
import com.nanfenggongxiang.Domain.CommodityExample;
import com.nanfenggongxiang.Service.commodityService;
import groovy.util.MapEntry;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by skyisbule on 2018/3/26.
 * 控制商品
 */
@Api(description = "商品相关的api")
@RestController
@RequestMapping(value = "",method = RequestMethod.POST)
public class shopController {

    @Autowired
    CommodityMapper dao;
    @Autowired
    CommodityDao    complexDao;
    @Autowired
    commodityService service;
    @Autowired
    countDao        commodity;

    /**
     * 增加一件商品，需要已经登录。
     * @param uid        从cookie中读取用户的id
     * @param commodity  商品实体类，注意我会做一些初始化。
     * @See   Domain.Commodity
     * @return 成功或失败
     */
    @ApiOperation("添加一条商品")
    @RequestMapping("/private/commodity/add")
    public String addCommodity(
            @CookieValue("uid")int uid,
            Commodity commodity){
        commodity.setUid(uid);
        //初始化
        init(commodity);
        Date date = new Date();
        commodity.setReleaseTime(date);
        if(dao.insert(commodity)==1){
            service.addReleaseNum(uid);
            return "success";
        }
        return "false";
    }


    /**
     *
     * 这个api用于首页，它会返回商品信息和发布者的信息。
     *
     * @param page      页码数，从0开始，每次给你返回10条
     * @param isWantBy  卖还是买    想买是1   想卖是0
     * @param isSellOut 交易是否关闭，即是否处在交易状态，1是上架中，0表示已经下架。
     * @return          一个json数组，内容为商品的实体
     */
    @ApiOperation("按页码数返回商品信息和发布者信息")
    @RequestMapping(value = "/public/commodity/get-with-info-by-page",method = RequestMethod.GET)
    public List<Map<String,Object>> getCommodityAndUserInfoByPage(
                                                                  @ApiParam("是否被卖出去了，传1和0")int isSellOut,
                                                                  @ApiParam("是想买还是想卖")int isWantBy,
                                                                  @ApiParam("页码数，从0开始") int page,
                                                                  @ApiParam("分类")int goodsType
                                                                  ){
        if (goodsType==0)
            return complexDao.getCommodityAndUserInfoByPage(isSellOut,isWantBy,page*10);
        else
            return complexDao.getCommodityAndUserInfoByPageAndType(isSellOut,isWantBy,page*10,goodsType);
    }

    @ApiOperation("通过商品id获取商品详情")
    @RequestMapping(value = "/public/commodity/get-with-info-by-gid",method = RequestMethod.GET)
    public Map<String,Object> getCommodityAndUserInfoByGid(@ApiParam("商品id")int gid){
        Map<String,Object> map = complexDao.getCommodityAndUserByGid(gid);
        if (map == null){
            return new HashMap<String,Object>();
        }
        String GoodsUid = map.get("uid").toString();
        Integer uid = Integer.parseInt(GoodsUid);
        Integer isSellOut = Integer.parseInt(map.get("is_sell_out").toString());
        //拿到在售的宝贝有多少件
        Object count = complexDao.getCommoditySellOutCount(uid,isSellOut);
        map.put("selling",count);
        //浏览量加一
        commodity.commodityPageViewsAdd(gid);
        return map;
    }

    @ApiOperation("获取某用户有多少宝贝在售")
    @RequestMapping(value = "/public/commodity/get-selling-count",method = RequestMethod.GET)
    public Integer getSellingCount(@ApiParam("用户id") int uid,
                                   @ApiParam("是否卖出") int isSellOut){
        return complexDao.getCommoditySellOutCount(uid,isSellOut);
    }

    @ApiOperation("搜索商品")
    @RequestMapping("/public/commodity/search")
    public List<Map<String,Object>> getCommodityAndUserInfoByPageAndTypeAndGoodsName(@ApiParam("是否被卖出去了，传1和0")int isSellOut,
                                                                   @ApiParam("是想买还是想卖")int isWantBy,
                                                                   @ApiParam("页码数，从0开始") int page,
                                                                   @ApiParam("分类")int goodsType,
                                                                   @ApiParam("名字")String goodsName){
        if (goodsName.startsWith("%")||goodsName.endsWith("%"))
            return new LinkedList<>();
        goodsName = "%"+goodsName+"%";
        List<Map<String,Object>> res = complexDao.getCommodityAndUserInfoByPageAndTypeAndGoodsName(isSellOut,isWantBy,page,goodsType,goodsName);
        //把查到的密码消掉
        if (res == null)
            return new LinkedList<>();
        for (Map map : res){
            map.put("passwd","***");
        }
        return res;
    }

    @ApiOperation("用于修改商品状态，即关闭交易")
    @RequestMapping("/private/commodity/close")
    public String close(int gid){
        return complexDao.tagSellOut(gid)==1?"success":"error";
    }

    @ApiOperation("我发布的，传uid和page给我")
    @RequestMapping("/private/commodity/my_release")
    public List<Map<String,Object>> myRelease(int uid,int isSellOut,int isWantBuy,int page){
        return complexDao.getMyRelease(uid,isSellOut,isWantBuy,page*10);
    }

    @ApiOperation("获取有多少条商品")
    @RequestMapping(value = "/public/commodity/get-count",method = RequestMethod.GET)
    public Integer getCount(){
        return complexDao.getTotalCommodityCount();
    }

    @ApiOperation("删除一件商品")
    @RequestMapping(value = "/admin/commodity/delete",method = {RequestMethod.GET,RequestMethod.POST})
    public String delete(int gid){
        return service.delectCommodity(gid)?"删除成功":"删除失败，一般情况为gid输入有误，数据库已回滚";
    }

    private void init(Commodity commodity){
        commodity.setGid(null);
        //commodity.setUid(uid);
        commodity.setPageViews(1);
        commodity.setReleaseTime(new Date());
        commodity.setIsSellOut(0);
    }


}
