package com.bitdubai.fermat_api.module_object_creator;

import java.io.Serializable;

/**
 * Created by mati on 2016.04.18..
 */
public class AbstractFermatModuleObject<O> implements FermatModuleObjectInterface{

    public AbstractFermatModuleObject(Serializable creator) {
        creator(creator);
    }

    @Override
    public void creator(Serializable objectCreator) {

    }

    @Override
    public int describeContent() {
        return 0;
    }

    @Override
    public Serializable writeToSerializable() {
        return null;
    }
}
