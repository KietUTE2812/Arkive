package com.example.arkivebackend.repository;

import com.example.arkivebackend.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {

    // Find assets by collectionId and sort by createdAt descending
    List<Asset> findByCollectionIdOrderByCreatedAtDesc(String collectionId);
    // Find assets by collectionId and sort by createdAt ascending
    List<Asset> findByCollectionIdOrderByCreatedAtAsc(String collectionId);

    // Find assets by collectionId and sort by filename ascending
    List<Asset> findByCollectionIdOrderByFilenameAsc(String collectionId);
    // Find assets by collectionId and sort by filename descending
    List<Asset> findByCollectionIdOrderByFilenameDesc(String collectionId);

    // Find assets by collectionId and sort by fileSize ascending
    List<Asset> findByCollectionIdOrderByFileSizeAsc(String collectionId);
    // Find assets by collectionId and sort by fileSize descending
    List<Asset> findByCollectionIdOrderByFileSizeDesc(String collectionId);

    // BEST PRACTICE: Pageination nên được sử dụng thay vì lấy tất cả bản ghi cùng một lúc
    @Query("SELECT a FROM Asset a WHERE a.collection.id = :collectionId AND lower(a.filename) LIKE lower(concat('%', :keyword, '%')) AND a.isDeleted = false")
    Page<Asset> searchAssets(
            @Param("collectionId") String collectionId,
            @Param("keyword") String keyword,
            Pageable pageable // Spring sẽ tự động thêm Sắp xếp (Sort) và Phân trang (Pagination)
    );

    /**
     * Tìm tất cả assets theo collection ID
     */
    List<Asset> findAllByCollectionId(String collectionId);

    /**
     * Tìm asset theo storage key
     */
    Optional<Asset> findByStorageKey(String storageKey);

    /**
     * Đếm số lượng assets trong một collection
     */
    long countByCollectionId(String collectionId);

    /**
     * Xóa tất cả assets trong một collection
     */
    void deleteAllByCollectionId(String collectionId);

    /**
     * Kiểm tra asset có tồn tại trong collection không
     */
    boolean existsByIdAndCollectionId(String assetId, String collectionId);

    /**
     * Kiểm tra xem user có phải owner của collection không.
     */
    boolean existsByCollectionIdAndCollectionOwnerUsername(String collectionId, String username);

    /**
     * Kiểm tra xem user có phải owner của asset không.
     */
    boolean existsByIdAndCollectionOwnerUsername(String assetId, String username);

}
