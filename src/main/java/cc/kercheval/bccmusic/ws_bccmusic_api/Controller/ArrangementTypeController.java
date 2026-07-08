package cc.kercheval.bccmusic.ws_bccmusic_api.Controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cc.kercheval.bccmusic.ws_bccmusic_api.Mapper.ArrangementTypeMapper;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.ArrangementType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Service.ArrangementTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/arrangement-types")
@RequiredArgsConstructor
public class ArrangementTypeController {
	
	private final ArrangementTypeService arrangementTypeService;
	private final ArrangementTypeMapper arrangementTypeMapper;
	
	@GetMapping
	public List<ArrangementType> getArrangementTypes() {
		return arrangementTypeService.getArrangementTypes()
				.stream()
				.map(arrangementTypeMapper::toDto)
				.sorted(Comparator.comparing(ArrangementType::getSortOrder))
				.toList();
	}
}
