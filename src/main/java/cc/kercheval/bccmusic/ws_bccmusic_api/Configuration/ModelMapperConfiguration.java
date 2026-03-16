package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ArrangementType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.Score;
import cc.kercheval.bccmusic.ws_bccmusic_api.Model.MusicScore;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.AccountRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ArrangementTypeRepository;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ScoreRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfiguration {
	
	private final ArrangementTypeRepository arrangementTypeRepository;

	@Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT)
              .setSkipNullEnabled(true)    
              .setAmbiguityIgnored(true);

        // Optional: Skip ID fields automatically (very useful!)
        mapper.getConfiguration().setPropertyCondition(ctx -> {
            String destName = ctx.getMapping().getLastDestinationProperty().getName();
            boolean isIdField =
                  ctx.getMapping().getLastDestinationProperty().getName().equals("id") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("accountId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("scoreId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("composerId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("collaboratorId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("medleyId") && 
                  ctx.getMapping().getLastDestinationProperty().getName().equals("partId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("scoreComposerId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("scoreTagId") &&
                  ctx.getMapping().getLastDestinationProperty().getName().equals("vendorId") && 
                  false;

            Converter<String, ArrangementType> converter = new AbstractConverter<String, ArrangementType>() {
                @Override
                protected ArrangementType convert(String code) {
                    if (code == null || code.isBlank()) {
                        return null; // or throw, depending on your rules
                    }
                    return arrangementTypeRepository.findById(code)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid arrangement type code: " + code));
                }
            };

            // Register the converter globally (optional but useful)
            mapper.addConverter(converter);
            
         // Skip ID if we're mapping TO an entity (destination type is your entity package)
            boolean toEntity = ctx.getDestinationType().getName().startsWith("cc.kercheval.bccmusic.ws_bccmusic_api.Entity"); 
            return !(isIdField && toEntity);
        }); 

        return mapper;
    }	
}
