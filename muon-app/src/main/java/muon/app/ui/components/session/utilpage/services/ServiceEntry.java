package muon.app.ui.components.session.utilpage.services;

public class ServiceEntry {
    private String name;
    private String unitStatus;
    private String desc;
    private String unitFileStatus;

    public ServiceEntry(String name, String unitStatus, String desc, String unitFileStatus) {
        this.name = name;
        this.unitStatus = unitStatus;
        this.desc = desc;
        this.unitFileStatus = unitFileStatus;
    }

    public ServiceEntry() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitStatus() {
        return unitStatus;
    }

    public void setUnitStatus(String unitStatus) {
        this.unitStatus = unitStatus;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUnitFileStatus() {
        return unitFileStatus;
    }

    public void setUnitFileStatus(String unitFileStatus) {
        this.unitFileStatus = unitFileStatus;
    }

    @Override
    public String toString() {
        return "ServiceEntry{" +
                "name='" + name + '\'' +
                ", unitStatus='" + unitStatus + '\'' +
                ", desc='" + desc + '\'' +
                ", unitFileStatus='" + unitFileStatus + '\'' +
                '}';
    }
}
