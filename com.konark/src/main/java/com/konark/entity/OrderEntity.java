package com.konark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Inventory_SKUS1")
public class OrderEntity {
@Id
    @Column(name="Store_ID")
    private String storeId;

    @Column(name="ItemNum")
    private String itemNum;

    @Column(name="AltSKU")
    private String altSKU;

}
