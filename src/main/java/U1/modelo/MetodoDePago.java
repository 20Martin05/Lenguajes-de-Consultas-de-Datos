package U1.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "metodo_de_pago")
@Getter
@Setter
@NoArgsConstructor
public class MetodoDePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String pago;

    @Column(length = 50)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado = true;

    @OneToMany(mappedBy = "metodoDePago")
    private Set<Factura> facturas = new HashSet<>();

    @Override
    public String toString() {
        return "MetodoDePago{id=" + id + ", pago='" + pago + "'}";
    }
}
