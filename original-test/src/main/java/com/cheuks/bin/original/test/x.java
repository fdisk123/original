package com.cheuks.bin.original.test;

import java.util.HashSet;
import java.util.Set;

public class x {

    public static void main(String[] args) {
        Set<String> x = new HashSet<String>();
        String str = "广东美的厨卫电器制造有限公司,佛山市美的清湖净水设备有限公司,佛山市顺德区美的饮水机制造有限公司,芜湖美的厨卫电器制造有限公司,无锡小天鹅股份有限公司,无锡飞翎电子有限公司,无锡小天鹅通用电器有限公司,小天鹅(荆州)三金电器有限公司,合肥美的洗衣机有限公司,合肥美的电冰箱有限公司,合肥华凌股份有限公司,合肥美的智能科技有限公司,湖北美的电冰箱有限公司,广州美的华凌冰箱有限公司,中国雪柜实业有限公司,江苏美的清洁电器股份有限公司,广东美芝制冷设备有限公司,广东美芝精密制造有限公司,佛山市威灵电子电器有限公司,广东威灵电机制造有限公司,佛山市威灵洗涤电机制造有限公司,广东美的智能科技有限公司,安徽美芝制冷设备有限公司,合肥威灵电机制造有限公司,安徽美芝精密制造有限公司,威灵(芜湖)电机制造有限公司,芜湖威灵电机销售有限公司,浙江美芝压缩机有限公司,淮安威灵电机制造有限公司,美的威灵电机技术(上海)有限公司,常州威灵电机制造有限公司,常州弘禄华特电机有限公司,安得智联科技股份有限公司,宁波安得智联科技有限公司,深圳美云智数科技有限公司,广东美的环境电器制造有限公司,宁波美美家园电器服务有限公司,美的集团电子商务有限公司,佛山市顺德区美的小额贷款股份有限公司,佛山市顺德区美的家电实业有限公司,美的集团财务有限公司,美的金融控股（深圳）有限公司,美的网络信息服务（深圳）有限公司,深圳市美的支付科技有限公司,美的商业保理有限公司,宁波美的小额贷款有限公司,美的小额贷款股份有限公司,广东美的厨房电器制造有限公司,广东美的微波炉制造有限公司,广东威特真空电子制造有限公司,佛山市顺德区美的洗涤电器制造有限公司,芜湖美的厨房电器制造有限公司,芜湖美的洗涤电器制造有限公司,芜湖美的洗涤电器商贸有限公司,广东美的生活电器制造有限公司,佛山市顺德区美的电热电器制造有限公司,广东美的酷晨生活电器制造有限公司,芜湖美的生活电器制造有限公司,武汉美的材料供应有限公司,主力智业(深圳)电器实业有限公司,广东美的暖通设备有限公司,广东美的希克斯电子有限公司,广东美的商用空调设备有限公司,合肥美的暖通设备有限公司,合肥美联博空调设备有限公司,江西美的贵雅照明有限公司,重庆美的通用制冷设备有限公司,广东美的制冷设备有限公司,佛山市美的开利制冷设备有限公司,佛山市顺德区百年科技有限公司,佛山市顺德区百年同创塑胶实业有限公司,佛山市顺德区美的电子科技有限公司,广东美的集团芜湖制冷设备有限公司,芜湖美智空调设备有限公司,芜湖乐祥电器有限公司,广州华凌制冷设备有限公司,重庆美的制冷设备有限公司,美的集团武汉制冷设备有限公司,邯郸美的制冷设备有限公司,美的集团股份有限公司";
        String[] strs = str.split(",");
        for (String s : strs) {
            x.add(s.trim());
        }
        System.out.println(x.size());
        
        String a="aaa.vvv.ccc.sss.xxxa";
        System.out.println(a.substring(0,a.lastIndexOf(".")));
        
    }

}