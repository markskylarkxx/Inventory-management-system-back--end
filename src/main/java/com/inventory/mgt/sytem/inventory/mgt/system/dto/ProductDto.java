package com.inventory.mgt.sytem.inventory.mgt.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.inventory.mgt.sytem.inventory.mgt.system.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder

public class ProductDto {
    @NotNull
    private String name;
    @Length(max = 15, min = 10)
    @Column(name = "serial_number")
    //@NotBlank(message = "insert product serial number!")
    private  String serialNumber;
   // @NotNull
    private String description;
  //  @NotBlank(message = "price cannot be blank")
    private Long  price;
   // @NotBlank(message = "quantity cannot be blank")
    private  Long quantity;
 // @JsonSerialize(as = LocalDate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dateOfManufacture;
    @JsonSerialize(as = LocalDate.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate expirationDate;
    private boolean isAvailable;
   // @NotBlank(message = "brand name cannot be blank")
    private  String brand;
    @Enumerated(EnumType.STRING)
    private Category category;

}
