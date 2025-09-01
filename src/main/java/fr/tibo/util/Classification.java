package fr.tibo.util;

public enum Classification {
	NA("-"), TOUS_PUBLICS("Tous publics"), DECONSEILLE_7_ANS("Déconseillé < 7 ans"),
	ACCOMPAGNE_12_ANS("Accompagné < 12 ans"), INTERDIT_16_ANS("Interdit < 16 ans");

	private String label;

	private Classification(String label) {
		this.label = label;
	}

	public String lbl() {
		return label;
	}

	@Override
	public String toString() {
		return label;
	}
}
