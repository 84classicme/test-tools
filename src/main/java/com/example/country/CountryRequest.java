package com.example.country;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ApiModel
public class CountryRequest {

    @ApiModelProperty(example = "Utopia")
    private String name;

    @ApiModelProperty(example = "Ritehere")
    private String capital;

    @ApiModelProperty(example = "1")
    private int population;

    @ApiModelProperty(example = "MGB")
    private String currency;
}
