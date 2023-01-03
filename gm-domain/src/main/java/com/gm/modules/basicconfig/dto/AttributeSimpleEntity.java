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
    private Long hp;
    /**
     * 法力值
     */
    private Long mp;
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
    private Long armor;
    /**
     * 魔抗
     */
    private Long magicResist;
    /**
     * 攻击力
     */
    private Long attackDamage;
    /**
     * 法攻
     */
    private Long attackSpell;

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
    public AttributeSimpleEntity(Long hp, Long mp, Double hpRegen, Double mpRegen, Long armor, Long magicResist, Long attackDamage, Long attackSpell) {
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
