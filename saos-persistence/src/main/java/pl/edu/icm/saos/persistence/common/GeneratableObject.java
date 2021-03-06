package pl.edu.icm.saos.persistence.common;

import javax.persistence.Transient;

import pl.edu.icm.saos.persistence.enrichment.model.EnrichmentTag;

/**
 * Objects of this class are ones generated from {@link EnrichmentTag}s. 
 * 
 * @author Łukasz Dumiszewski
 */

public abstract class GeneratableObject implements Generatable {

    
    private boolean generated = false;
    
    
    
    //------------------------ LOGIC --------------------------
    
    @Override
    @Transient
    public boolean isGenerated() {
        return this.generated;
        
    }

    @Override
    public void markGenerated() {
        this.generated = true;
        
    }

}
