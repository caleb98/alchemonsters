package com.ccode.alchemonsters.creature.equip;

public class Amplifier {

	public Affix[] prefixes;
	public Affix[] suffixes;
	
	public Amplifier(Affix pre, Affix suff) {
		prefixes = new Affix[]{ pre };
		suffixes = new Affix[]{ suff };
	}
	
	public Amplifier(Affix pre1, Affix pre2, Affix suff1, Affix suff2) {
		prefixes = new Affix[]{ pre1, pre2 };
		suffixes = new Affix[]{ suff1, suff2 };
	}
	
}
