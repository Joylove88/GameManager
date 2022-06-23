package com.gm.modules.basicconfig.dto;

import java.io.Serializable;
import java.util.Random;

/**
 * 
 * 
 * @author Axiang
 * @email Axiang@gmail.com
 * @date 2022-01-23 19:06:59
 */
public class BattleTestEntity implements Serializable {
	//创建三个属性
	private String[] name;
	private String[] skill;
	private long[] skillharm;
	private double maxhp;
	//重载，便于调用，再多态调用下更加灵活
	public BattleTestEntity(String[] name,String[] skill,long[] skillharm,long maxhp){
		this.name=name;
		this.skill=skill;
		this.skillharm=skillharm;

		this.maxhp=maxhp;
	}

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public String[] getSkill() {
		return skill;
	}

	public void setSkill(String[] skill) {
		this.skill = skill;
	}

	public long[] getSkillharm() {
		return skillharm;
	}

	public void setSkillharm(long[] skillharm) {
		this.skillharm = skillharm;
	}

	public double getMaxhp() {
		return maxhp;
	}

	public void setMaxhp(double maxhp) {
		this.maxhp = maxhp;
	}


	public void attck(BattleTestEntity a){
		//用Random随机选择电脑需要进行的操作
		Random ra = new Random();
		int r = ra.nextInt(4);

		if(a.maxhp>=0){
			if(r==1){
				a.maxhp = a.maxhp - skillharm[0];
				a.maxhp = a.maxhp + (a.maxhp * 0.01); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[0]+"，"+"对"+a.name[r]+"造成"+skillharm[0]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else if(r==2){
				a.maxhp=a.maxhp-skillharm[1];
				a.maxhp = a.maxhp + (a.maxhp * 0.01); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[1]+"，"+"对"+a.name[r]+"造成"+skillharm[1]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else if(r==3){
				a.maxhp=a.maxhp-skillharm[2];
				a.maxhp = a.maxhp + (a.maxhp * 0.01); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[2]+"，"+"对"+a.name[r]+"造成"+skillharm[2]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else if(r==4){
				a.maxhp=a.maxhp-skillharm[3];
				a.maxhp = a.maxhp + (a.maxhp * 0.01); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[3]+"，"+"对"+a.name[r]+"造成"+skillharm[3]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");
			}

			if(a.maxhp<=0){
				System.out.println(name[r]+"打死了"+a.name[r]+"-----"+"战斗胜利"+"-----"+name+"胜利");
			}
		}
	}

	public void attck1(BattleTestEntity a){
		//用Random随机选择电脑需要进行的操作
		Random ra = new Random();
		int r = ra.nextInt(4);
		if(a.maxhp>=0){
			if(r==0){
				a.maxhp=a.maxhp-skillharm[0];
				a.maxhp = a.maxhp + (a.maxhp * 0.1); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[0]+"，"+"对"+a.name[r]+"造成"+skillharm[0]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else if(r==1){
				a.maxhp=a.maxhp-skillharm[1];
				a.maxhp = a.maxhp + (a.maxhp * 0.1); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[1]+"，"+"对"+a.name[r]+"造成"+skillharm[1]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else if(r==2){
				a.maxhp=a.maxhp-skillharm[2];
				a.maxhp = a.maxhp + (a.maxhp * 0.1); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[2]+"，"+"对"+a.name[r]+"造成"+skillharm[2]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");

			}else{
				a.maxhp=a.maxhp-skillharm[3];
				a.maxhp = a.maxhp + (a.maxhp * 0.1); // 每次攻击后恢复的HP
				System.out.println(name[r]+"使用"+"，"+skill[3]+"，"+"对"+a.name[r]+"造成"+skillharm[3]+"的伤害，"+a.name[r]+"剩余"+a.maxhp+"的血量");
			}

			if(a.maxhp<=0){
				System.out.println(name[r]+"打死了"+a.name[r]+"-----"+"战斗胜利"+"-----"+name[r]+"胜利");
			}
		}
	}

	public static void main(String[] args) {

		String[] name={"二狗","二狗2","二狗3","二狗4"};
		String[] skill={"狗王击","哮天降世","旋风狗腿","召唤"};
		long[] skillharm={20,300,50,100};

		BattleTestEntity ko=new BattleTestEntity(name,skill,skillharm,1000L);

		String[] name1={"二牛","二牛2","二牛3","二牛4"};
		String[] skill1={"小牛向前冲","牛气冲天","死亡碰撞","牛王附体"};
		long[] skillharm1={30,50,80,350};

		BattleTestEntity ko1=new BattleTestEntity(name1,skill1,skillharm1,1000L);

//		Scanner sc = new Scanner(System.in);
//		int a = sc.nextInt();
		System.out.println("请选择英雄,1,二狗----2，二牛");
		System.out.println("您选择了二狗");

		while(true){
//			System.out.println("二狗的技能有："+"1.狗王击"+"2.哮天降世"+"3.旋风狗腿"+"4.召唤");

			if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;

			ko.attck(ko1);

			if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;

			ko1.attck1(ko);

			if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
		}
//
//		if(a==1){
//			System.out.println("您选择了二狗");
//
//			while(true){
//				System.out.println("二狗的技能有："+"1.狗王击"+"2.哮天降世"+"3.旋风狗腿"+"4.召唤");
//
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//
//				ko.attck(ko1);
//
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//
//				ko1.attck1(ko);
//
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//			}
//
//		}else if(a==2){
//			System.out.println("您选择了二牛");
//			while(true){
//				System.out.println("二牛的技能有："+"1.小牛向前冲"+"2.牛气冲天"+"3.死亡碰撞"+"4.牛王附体");
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//
//				ko1.attck(ko);
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//
//				ko.attck1(ko1);
//				if(ko1.getMaxhp()<=0||ko.getMaxhp()<=0)break;
//
//			}
//		}

	}

}
