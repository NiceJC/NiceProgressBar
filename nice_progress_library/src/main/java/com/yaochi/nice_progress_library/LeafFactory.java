package com.yaochi.nice_progress_library;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeafFactory {
    private   int MAX_LEAF_NUM=6;
    public static final int CYCLE_MILLI=3000;//叶子飞行一个周期的时间（与等待时间的最大值相同 叶子连续效果最佳）
    Random random = new Random();

    private LeafBean GenerateLeaf(){
        LeafBean leafBean=new LeafBean();
        leafBean.yFactory=(float) (random.nextInt(20)-10)/10;//摆动因子 范围在-1 到 1
        leafBean.rotateDirection=random.nextInt(2);
        leafBean.rotateAngle=random.nextInt(360);
        leafBean.waitTime=random.nextInt(CYCLE_MILLI);

        return leafBean;
    }

    public List<LeafBean> generateLeafs(){
        List<LeafBean> leafs=new ArrayList<>();
        for (int i = 0; i <MAX_LEAF_NUM ; i++) {
            leafs.add(GenerateLeaf());
        }
        return leafs;
    }
}
