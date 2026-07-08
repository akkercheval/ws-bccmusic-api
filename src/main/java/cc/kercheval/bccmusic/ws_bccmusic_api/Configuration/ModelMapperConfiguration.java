package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cc.kercheval.bccmusic.ws_bccmusic_api.Entity.ArrangementType;
import cc.kercheval.bccmusic.ws_bccmusic_api.Repository.ArrangementTypeRepository;
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

        // Register the ArrangementType converter once
        Converter<String, ArrangementType> converter = new AbstractConverter<String, ArrangementType>() {
            @Override
            protected ArrangementType convert(String code) {
                if (code == null || code.isBlank()) {
                    return null;
                }
                return arrangementTypeRepository.findById(code)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid arrangement type code: " + code));
            }
        };
        mapper.addConverter(converter);

        // Skip ID fields when mapping TO an entity
        mapper.getConfiguration().setPropertyCondition(ctx -> {
            String destName = ctx.getMapping().getLastDestinationProperty().getName();
            boolean isIdField =
                  destName.equals("id") ||
                  destName.equals("accountId") ||
                  destName.equals("scoreId") ||
                  destName.equals("composerId") ||
                  destName.equals("collaboratorId") ||
                  destName.equals("medleyId") || 
                  destName.equals("partId") ||
                  destName.equals("scoreComposerId") ||
                  destName.equals("scoreTagId") ||
                  destName.equals("vendorId");

            // Skip ID if we're mapping TO an entity (destination type is your entity package)
            boolean toEntity = ctx.getDestinationType().getName().startsWith("cc.kercheval.bccmusic.ws_bccmusic_api.Entity"); 
            return !(isIdField && toEntity);
        }); 

        return mapper;
    }	
}
