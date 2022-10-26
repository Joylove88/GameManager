package com.gm.modules.basicconfig.dto;

import lombok.Data;

/**
 * Created by Axiang on 2022/6/6 0006.
 */
@Data
public class AttributeSimpleEntity {
    /**
     * 初始生命值
     */
    private Long hp;
    /**
     * 初始法力值
     */
    private Long mp;
    /**
     * 初始生命值恢复
     */
    private Double hpRegen;
    /**
     * 初始法力值恢复
     */
    private Double mpRegen;
    /**
     * 初始护甲
     */
    private Long armor;
    /**
     * 初始魔抗
     */
    private Long magicResist;
    /**
     * 初始攻击力
     */
    private Long attackDamage;
    /**
     * 初始法攻
     */
    private Long attackSpell;
    /**
     * 英雄星级
     */
    private Integer starCode;
    /**
     * 通过星级计算出对应的属性
     * @param starCode
     * @param starRate
     * @param hp
     * @param mp
     * @param hpRegen
     * @param mpRegen
     * @param armor
     * @param magicResist
     * @param attackDamage
     * @param attackSpell
     */
    public AttributeSimpleEntity(Double starRate,Integer starCode, Long hp, Long mp, Double hpRegen, Double mpRegen, Long armor, Long magicResist, Long attackDamage, Long attackSpell) {
        this.starCode = starCode;
        this.hp = Math.round(hp * starRate);
        this.mp = Math.round(mp * starRate);
        this.hpRegen = (double) (Math.round(hpRegen * starRate)) / 100;
        this.mpRegen = (double) (Math.round(mpRegen * starRate)) / 100;
        this.armor = Math.round(armor * starRate);
        this.magicResist = Math.round(magicResist * starRate);
        this.attackDamage = Math.round(attackDamage * starRate);
        this.attackSpell = Math.round(attackSpell * starRate);
    }
}
