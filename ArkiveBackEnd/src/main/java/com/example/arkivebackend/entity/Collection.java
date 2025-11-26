package com.example.arkivebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true) // Kế thừa equals và hashCode từ BaseEntity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collections", schema = "arkive",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_collection_name_owner", // Tên của ràng buộc
                        columnNames = {"name", "owner_id"} // Cặp cột phải là duy nhất
                )
        }
)
public class Collection extends  BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    // --- Relationships

    @ManyToOne(fetch = FetchType.LAZY) // Nhiều bộ sưu tập có thể thuộc về một người dùng
    @JoinColumn(name = "owner_id", nullable = false) // Khóa ngoại tham chiếu đến bảng User
    private User owner;

    @OneToMany(
            fetch = FetchType.LAZY, // Tải lười các tài sản khi cần (lazy loading)
            mappedBy = "collection", // Thuộc tính trong lớp Asset tham chiếu đến Collection
            cascade = CascadeType.ALL, // Thao tác trên Collection sẽ lan truyền đến các Asset liên quan
            orphanRemoval = true) // Xóa các Asset không còn liên kết với Collection
    @Builder.Default // Đảm bảo rằng danh sách không bao giờ là null
    private List<Asset> assets = new ArrayList<>();

    // Helper method to add an asset to the collection
    public void addAsset(Asset asset) {
        assets.add(asset);
        asset.setCollection(this);
    }

    // Helper method to remove an asset from the collection
    public void removeAsset(Asset asset) {
        assets.remove(asset);
        asset.setCollection(null);
    }
}
