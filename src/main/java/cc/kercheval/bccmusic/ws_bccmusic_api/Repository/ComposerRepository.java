package cc.kercheval.bccmusic.ws_bccmusic_api.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Composer;

public interface ComposerRepository extends JpaRepository<Composer, Long> {

}
