package demo.domain.entity;

import com.zhukai.spring.integration.commons.annotation.Entity;
import com.zhukai.spring.integration.commons.annotation.Id;

/**
 * Created by zhukai on 17-2-8.
 */
@Entity
public class FoodBean {

    @Id
    private String name;

    private Float price;

    private Integer quantity;

    public FoodBean() {
    }

    public FoodBean(String name, Float price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
