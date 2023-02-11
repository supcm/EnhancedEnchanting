package net.supcm.enhancedenchanting.common.exaltation;

import com.google.common.collect.Lists;
import net.supcm.enhancedenchanting.common.exaltation.exaltations.*;
import java.util.List;
import java.util.Random;

public class Exaltations {
    private static final List<Exaltation> exaltations = Lists.newArrayList();
    public static void registerExaltations() {
        registerExaltation(new FireExaltation());
        registerExaltation(new PoisonExaltation());
        registerExaltation(new HammerExaltation());
        registerExaltation(new DamageBoostExaltation());
        registerExaltation(new LightningExaltation());
        registerExaltation(new EnderlichExaltation());

        //maybe later
        //registerExaltation(new IceSealExaltation());
        //registerExaltation(new DoubleUseExaltation());
    }
    public static void registerExaltation(Exaltation ex) {
        if(!exaltations.contains(ex))
            exaltations.add(ex);
    }
    public static Exaltation createRandomExaltation(Random random) {
        return exaltations.get(random.nextInt(exaltations.size()));
    }
    public static Exaltation getExaltationByName(String name) {
        if(exaltations.size() > 0) {
            for(Exaltation ex : exaltations)
                if(ex.getName().equals(name))
                    return ex;
        }
        return null;
    }
}
