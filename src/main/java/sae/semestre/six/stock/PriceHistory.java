package sae.semestre.six.stock;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
    
    @Column(name = "old_price")
    private Double oldPrice;
    
    @Column(name = "new_price")
    private Double newPrice;
    
    @Column(name = "change_date")
    private LocalDate changeDate = LocalDate.now();

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }

    public void setOldPrice(Double oldPrice) {
        this.oldPrice = oldPrice;
    }

    // TODO change name to getPriceChange
    public Double getPriceIncrease() {
        return newPrice - oldPrice;
    }
    
    public Double getPercentageChange() {
        return (newPrice - oldPrice) / oldPrice * 100;
    }
} 