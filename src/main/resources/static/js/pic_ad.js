"use strict";
(function () {
    var vw = document.body.clientWidth;
    document.write('<style type="text/css">#news {position:absolute;width:100vw;height:52vw;top:14vw;left:0;overflow-x:hidden;z-index: 10000;}#news a{position: absolute;width:100vw;height:100%;top:0;left:0;background-size:100% 100%;background-repeat:no-repeat;transform:translate(100vw,0);-webkit-transform:translate(100vw,0)}@keyframes move_news_2{to{transform:translate(0,0)}}@-webkit-keyframes move_news_2{to{-webkit-transform:translate(0,0)}}@keyframes move_news_1{to{transform:translate(-100vw,0)}}@-webkit-keyframes move_news_1{to{-webkit-transform:translate(-100vw,0)}}@keyframes move_news_3{to{transform:translate(100vw,0)}}@-webkit-keyframes move_news_3{to{-webkit-transform:translate(100vw,0)}}</style><div id="news"></div>');
    var a = new XMLHttpRequest();
    a.open("POST", "/public/notice/get-show");
    a.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    a.onreadystatechange = function () {
        if (a.readyState === 4 && (a.status === 200 || a.status === 304)) {
            var e = JSON.parse(a.responseText), c = "", d = e.length, b = function (g, f) {
                c += '<a target="new" href="' + e[f]["links"] + '" style="background-image: url(' + e[f]["picUrl"] + ')" data-id="' + f + '"></a>'
            };
            switch (d) {
                case 1:
                    b(0, 0);
                    b(0, 0);
                    b(0, 0);
                    d = 3;
                    break;
                case 2:
                    b(0, 0);
                    b(0, 1);
                    b(0, 0);
                    b(0, 1);
                    d = 4;
                    break;
                default:
                    e.forEach(b)
            }
            document.getElementById("news").innerHTML = c;
            setTimeout(function () {
                var h = document.getElementById("news").getElementsByTagName("a"), f = function (n, m) {
                    var l = "";
                    l = "move_news_" + (m + 2) + " 0.3s forwards";
                    n.animation = l;
                    n.WebkitAnimation = l;
                    setTimeout(function () {
                        n.transform = "translate(" + (m * 100) + "vw,0)";
                        n.webkitTransform = "transform:translate(" + (m * 100) + "vw,0)";
                        n.animation = "";
                        n.WebkitAnimation = ""
                    }, 300)
                }, j = function (m, l) {
                    m.transform = "translate(" + l + "vw,0)";
                    m.webkitTransform = "transform:translate(" + l + "vw,0)"
                }, i = 0, g = function (m, n) {
                    m = m || 30;
                    n = n || [h[(i + d - 1) % d].style, h[i].style, h[(i + 1) % d].style];
                    if (m > 20) {
                        j(n[0], 100);
                        f(n[1], -1);
                        f(n[2], 0);
                        i = (i + 1) % d
                    } else {
                        if (m < -20) {
                            j(h[(i + d - 2) % d].style, -100);
                            f(n[1], 1);
                            f(n[0], 0);
                            i = (i + d - 1) % d
                        } else {
                            f(n[0], -1);
                            f(n[1], 0);
                            f(n[2], 1)
                        }
                    }
                }, k;
                f(h[0].style, 0);
                if (d <= 1) {
                    return
                }
                k = setInterval(g, 3000);
                document.getElementById("news").ontouchstart = function (m) {
                    clearInterval(k);
                    var n = m.touches[0].clientX, l = [h[(i + d - 1) % d].style, h[i].style, h[(i + 1) % d].style];
                    document.getElementById("news").ontouchmove = function (p) {
                        var o = 100 * (p.touches[0].clientX - n) / vw;
                        j(l[0], o - 100);
                        j(l[1], o);
                        j(l[2], o + 100)
                    };
                    document.getElementById("news").ontouchend = function (o) {
                        var p = 100 * (n - o.changedTouches[0].clientX) / vw;
                        g(p, l);
                        k = setInterval(g, 3000);
                        document.getElementById("news").ontouchmove = null
                    }
                }
            }, 10)
        }
    };
    a.send("isShow=1")
})();