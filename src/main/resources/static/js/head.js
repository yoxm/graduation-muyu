"use strict";
var doc = document, page_num = 0, isWantBy = null, vw = doc.body.clientWidth, choose_dom, hchoose, goods_finish = false,
    timer = null, uid = pengge.cookie.get("uid"), z = false, hgoods;

function head(b) {
    var d = '<div id="choose" style="position:fixed;width:100vw;top:0;left:0;height:11vw;z-index:100;text-align:center;line-height:8vw;font-size:4vw;border-bottom:0.2vw solid #ccc;background-color: #fff">',
        a = b.length;
    for (var c = 0; c < a; c++) {
        d += '<span ' + ((a > 1) ? 'onClick="choose(' + c + ')" ' : '') + 'style="position:absolute;width:20vw;height:8vw;bottom:1vw;z-index:101;left: ' + (35 * (2 * c + 1) / a - 10 + 15) + 'vw" >' + b[c] + "</span>"
    }
    document.write(d + '<svg onclick="pengge.back_close()" style="position: absolute;left: 0;top: 0;width: 5vw;height: 5vw;margin: 3vw;"  viewBox="0 0 1024 1024"><path d="M363.840919 472.978737C336.938714 497.358861 337.301807 537.486138 364.730379 561.486138L673.951902 832.05497C682.818816 839.813519 696.296418 838.915012 704.05497 830.048098 711.813519 821.181184 710.915012 807.703582 702.048098 799.94503L392.826577 529.376198C384.59578 522.174253 384.502227 511.835287 392.492414 504.59418L702.325747 223.807723C711.056111 215.895829 711.719614 202.404616 703.807723 193.674252 695.895829 184.943889 682.404617 184.280386 673.674253 192.192278L363.840919 472.978737Z"></path></svg></div>');
    choose_dom = doc.getElementById("choose").getElementsByTagName("span");
    hchoose = doc.getElementById("choose").style;
    if (z) {
        document.write('<div id="goods"></div><div style="position: absolute;top: 35vw;height: 100vh;width: 1vw;"></div>');
        hgoods = doc.getElementById("goods");
        if (uid === null) {
            location.href = "/sign_in"
        }
    }
}

function choose(b, f) {
    if (isWantBy === b && f !== true) {
        return
    }
    var d = choose_dom[b].style;
    window.scrollTo(0, 0);
    d.color = "rgb(211,23,18)";
    d.borderBottom = "1vw solid rgb(211,23,18)";
    d.bottom = "0";
    if (f !== true) {
        d = choose_dom[isWantBy].style;
        d.color = "";
        d.borderBottom = "none";
        d.bottom = "1vw"
    }
    isWantBy = b;
    if (z) {
        hgoods.innerHTML = '<div class="card goods" id="loading">正在加载……</div>';
        get_goods()
    }
};