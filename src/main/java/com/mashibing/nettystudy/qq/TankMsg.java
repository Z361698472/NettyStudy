package com.mashibing.nettystudy.qq;

/**
 * @Auther: TianHao
 * @Date: 2021/6/25 - 06 - 25 - 17:12
 * @Description: com.mashibing.nettystudy.qq
 * @version: 1.0
 */
public class TankMsg {
    public int x, y;

    public TankMsg(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMsg:" + x + "," + y;
    }
}
