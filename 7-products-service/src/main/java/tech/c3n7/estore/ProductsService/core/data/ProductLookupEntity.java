package tech.c3n7.estore.ProductsService.core.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name="productlookup")
public class ProductLookupEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6704470838359508602L;

    @Id
    private String productId;

    @Column(unique = true)
    private String title;
}
