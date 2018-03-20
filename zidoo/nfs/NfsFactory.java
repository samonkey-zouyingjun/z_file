package zidoo.nfs;

import android.content.Context;
import zidoo.model.BoxModel;

public final class NfsFactory {
    public static NfsManager getNfsManager(Context context) {
        return getNfsManager(context, BoxModel.getModelCode(context));
    }

    public static NfsManager getNfsManager(Context context, int model) {
        switch (model) {
            case 1:
                return new MstarNfsManager(context);
            case 2:
                return new H3NfsManager(context);
            case 3:
            case 7:
            case BoxModel.MODEL_ROCKCHIP_SDK_23 /*3001*/:
                return new RockNfsManager(context);
            case 4:
                return new AmlogicNfsManager(context);
            case 5:
                return new RTD1295NfsManager(context);
            case 6:
                return new Amlogic905xNfsManager(context);
            case 8:
                return new Rock3328NfsManager(context);
            case 9:
                return new H6NfsManager(context);
            default:
                return new DefaultNfsManager(context);
        }
    }

    public static MstarNfsManager MSTART_MANAGER(Context context) {
        return new MstarNfsManager(context);
    }

    public static H3NfsManager H3_MANAGER(Context context) {
        return new H3NfsManager(context);
    }

    public static DefaultNfsManager DEFAULT_MANAGER(Context context) {
        return new DefaultNfsManager(context);
    }
}
