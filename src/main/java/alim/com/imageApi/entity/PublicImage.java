package alim.com.imageApi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "public_image")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "public_image_id")
    private Long publicImageId;

    @OneToOne
    @JoinColumn(name = "image_data_id")
    private ImageData imageData;
}
