package csulb.cecs323.model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table (name = "drugs")
public class Drug
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long did;

    private String chemical_name;
    private String description;

    // ASSOCIATION(S)
    @ManyToOne
    private DEA_Class schedule;
}
