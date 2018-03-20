package zidoo.tarot.kernel;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MaterialGroup implements Cloneable {
    private float mBodyAlpha;
    private HashMap<String, Material> mMaterialGroup;

    public MaterialGroup() {
        this.mBodyAlpha = 1.0f;
        this.mMaterialGroup = null;
        this.mMaterialGroup = new HashMap();
    }

    public MaterialGroup clone() {
        MaterialGroup cloned;
        try {
            cloned = (MaterialGroup) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            cloned = new MaterialGroup();
        }
        cloned.mMaterialGroup.clone();
        return cloned;
    }

    public Material getMaterial(String materialName) {
        return (Material) this.mMaterialGroup.get(materialName);
    }

    public List<Material> getMaterials() {
        return (List) this.mMaterialGroup.values();
    }

    public int size() {
        return this.mMaterialGroup.size();
    }

    public void addMaterial(Material material) {
        String materialName = material.MaterialName;
        if (materialName == null || materialName.equals("")) {
            materialName = "material";
        }
        this.mMaterialGroup.put(materialName, material);
    }

    public void clear() {
        recycle();
        this.mMaterialGroup.clear();
    }

    public void setAlpha(float alpha) {
        this.mBodyAlpha = alpha;
    }

    public float getAlpha() {
        return this.mBodyAlpha;
    }

    public void recycle() {
        for (Entry<String, Material> entry : this.mMaterialGroup.entrySet()) {
            ((Material) entry.getValue()).recycle();
        }
    }
}
