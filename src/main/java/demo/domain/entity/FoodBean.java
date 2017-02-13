package demo.domain.entity;

import com.zhukai.spring.integration.commons.annotation.*;

/**
 * Created by zhukai on 17-2-8.
 */
@Entity(name = "FOOD", indexes = {@Index(columns = {"name"})})
public class FoodBean {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", length = 32)
    private String name;

    @Column(name = "PRICE")
    private Float price;
    
    @Column(name = "QUANTITY")
    private Integer quantity;

    public FoodBean() {
    }

    public FoodBean(String name, Float price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "FoodBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
