package com.inventory.mgt.sytem.inventory.mgt.system.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.Category;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "product",schema = "inventory_mgt")
public class Product {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private  Long id;
    private String serialNumber;
    @Column(name = "name", nullable = false, length = 255)
    private String productName;
    @Column(name = " description", nullable = false)
    private String productDescription;
    @Column(name = "available")
    private boolean isAvailable;
    private Long quantity;
    @Column(name = "date_of_manufacture")

    private LocalDate dateOfManufacture;
    @Column(name = "expiration_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    @Column(name = "brand_name")
    private  String brandName;
    @Column(name = "price", nullable = false)
    private Long productPrice;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private  Category category;



    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "transactionId", referencedColumnName = "id")
    private  SalesTransaction transaction;


    public Product(String name,
                   String serialNumber,
                   String description,
                   Long quantity,
                   LocalDate  dateOfManufacture,
                   LocalDate expirationDate,
                   String brand, Long price, Category category) {
        this.productName = name;
        this.serialNumber = serialNumber;
        this.productDescription = description;
        this.quantity = quantity;
        this.dateOfManufacture = dateOfManufacture;
        this.expirationDate = expirationDate;
        this.brandName = brand;
        this.productPrice = price;
        this.category = category;
    }


}
