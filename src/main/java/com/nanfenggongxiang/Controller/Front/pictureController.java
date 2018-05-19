package com.nanfenggongxiang.Controller.Front;


import com.qiniu.util.Auth;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class pictureController {

    @RequestMapping("/public/pic/token")
    public String getToken(String fileName) {
        String accessKey = "WJ5rAAgin6kZz6ytY_wZbIL5_gy2-_wiaBrw0BOF";
        String secretKey = "KKThtAQ_pCIeVTgycLEIjCdlU_b4gaIkfmsIoFlO";
        String bucket = "";
        String key = fileName;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket, key);
        return upToken;
    }
}
