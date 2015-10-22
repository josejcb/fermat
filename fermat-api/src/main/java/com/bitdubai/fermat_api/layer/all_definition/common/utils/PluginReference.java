package com.bitdubai.fermat_api.layer.all_definition.common.utils;

import com.bitdubai.fermat_api.layer.all_definition.common.interfaces.FermatPluginsEnum;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;

/**
 * The class <code>com.bitdubai.fermat_api.layer.all_definition.common.utils.PluginReference</code>
 * haves all the information of a PluginReference.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 20/10/2015.
 */
public class PluginReference {

    private static final int HASH_PRIME_NUMBER_PRODUCT = 1523;
    private static final int HASH_PRIME_NUMBER_ADD = 2819;

    private final FermatPluginsEnum plugin ;
    private final Version           version;

    public PluginReference(final FermatPluginsEnum plugin,
                           final Version version) {

        this.plugin  = plugin ;
        this.version = version;
    }

    public FermatPluginsEnum getPlugin() {
        return plugin;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof PluginReference))
            return false;
        PluginReference compare = (PluginReference) o;

        return plugin.equals(compare.getPlugin()) &&
                version.equals(compare.getVersion());
    }

    @Override
    public int hashCode() {
        int c = 0;
        c += plugin .hashCode();
        c += version.hashCode();
        return 	HASH_PRIME_NUMBER_PRODUCT * HASH_PRIME_NUMBER_ADD + c;
    }
}
