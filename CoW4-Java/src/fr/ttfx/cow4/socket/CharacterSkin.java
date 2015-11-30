package fr.ttfx.cow4.socket;

/**
 * Created by Arsenik on 21/08/15.
 */
public enum CharacterSkin {
    WIZARD(0),
    DWARF(1),
    ELF(2),
    PULLET(3);

    private int id;

    CharacterSkin(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
