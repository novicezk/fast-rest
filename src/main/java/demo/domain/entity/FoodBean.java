package demo.domain.entity;

import com.zhukai.spring.integration.annotation.jpa.*;

/**
 * Created by zhukai on 17-2-8.
 */
@Entity(indexes = {@Index(columns = {"name"})})
public class FoodBean {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 32)
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
