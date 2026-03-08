package cc.kercheval.bccmusic.ws_bccmusic_api.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ArrangementType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ArrangementTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArrangementTypeService {
	private final ArrangementTypeRepository arrangementTypeRepository;
	
	public List<ArrangementType> getArrangementTypes() {
		Iterable<ArrangementType> arrangementTypes = arrangementTypeRepository.findAll();
		
		return StreamSupport.stream(arrangementTypes.spliterator(), false)
                .collect(Collectors.toList());
	}
}
