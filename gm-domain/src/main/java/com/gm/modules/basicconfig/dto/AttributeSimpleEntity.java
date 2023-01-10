package com.gm.modules.basicconfig.dto;

import lombok.Data;

/**
 * Created by Axiang on 2022/6/6 0006.
 */
@Data
public class AttributeSimpleEntity {
    /**
     * 生命值
     */
    private Double hp;
    /**
     * 法力值
     */
    private Double mp;
    /**
     * 生命值恢复
     */
    private Double hpRegen;
    /**
     * 法力值恢复
     */
    private Double mpRegen;
    /**
     * 护甲
     */
    private Double armor;
    /**
     * 魔抗
     */
    private Double magicResist;
    /**
     * 攻击力
     */
    private Double attackDamage;
    /**
     * 法攻
     */
    private Double attackSpell;

    /**
     * 通过星级计算出对应的属性
     * @param hp
     * @param mp
     * @param hpRegen
     * @param mpRegen
     * @param armor
     * @param magicResist
     * @param attackDamage
     * @param attackSpell
     */
    public AttributeSimpleEntity(Double hp, Double mp, Double hpRegen, Double mpRegen, Double armor, Double magicResist, Double attackDamage, Double attackSpell) {
        this.hp = hp;
        this.mp = mp;
        this.hpRegen = hpRegen;
        this.mpRegen = mpRegen;
        this.armor = armor;
        this.magicResist = magicResist;
        this.attackDamage = attackDamage;
        this.attackSpell = attackSpell;
//        this.attackSpell = Math.round(attackSpell * starRate);
    }
}
