package com.ccode.alchemonsters.creature;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ccode.alchemonsters.util.Pair;

public class CreatureBase implements Serializable {
	
	public String id;
	public String name;
	public String desc;
	public int minBaseHealth;
	public int maxBaseHealth;
	public int minBaseMana;
	public int maxBaseMana;
	public int baseVitae;
	public int baseFocus;
	public int baseMagicAtk;
	public int baseMagicDef;
	public int basePhysAtk;
	public int basePhysDef;
	public int basePenetration; //hehe
	public int baseResistance;
	public int baseSpeed;
	public ElementType[] types;
	public ArrayList<Pair<ElementType, Integer>> componentCost;
	
	public CreatureBase() {
		componentCost = new ArrayList<Pair<ElementType, Integer>>();
	}
	
	public CreatureBase(String id, String name, int minBaseHealth, int maxBaseHealth, int minBaseMana, int maxBaseMana, ElementType[] types, Pair<ElementType, Integer>... componentCost) {
		this.id = id;
		this.name = name;
		this.desc = "";
		this.minBaseHealth = minBaseHealth;
		this.maxBaseHealth = maxBaseHealth;
		this.minBaseMana = minBaseMana;
		this.maxBaseMana = maxBaseMana;
		this.types = types;
		
		this.componentCost = new ArrayList<Pair<ElementType, Integer>>();
		for(Pair<ElementType, Integer> p : componentCost) {
			this.componentCost.add(p);
		}
	}
	
	/*
	 * SERIALIZATION METHODS
	 */
	
	@Override
	public void write(Json json) {
		json.writeValue("id", id);
		json.writeValue("name", name);
		json.writeValue("desc", desc);
		json.writeValue("minBaseHealth", minBaseHealth);
		json.writeValue("maxBaseHealth", maxBaseHealth);
		json.writeValue("minBaseMana", minBaseMana);
		json.writeValue("maxBaseMana", maxBaseMana);
		json.writeValue("types", types);
		json.writeObjectStart("componentCost");
		for(Pair<ElementType, Integer> c : componentCost) {
			json.writeValue(c.a.toString(), c.b);
		}
		json.writeObjectEnd();
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		id = jsonData.getString("id");
		name = jsonData.getString("name");
		desc = jsonData.getString("desc");
		minBaseHealth = jsonData.getInt("minBaseHealth");
		maxBaseHealth = jsonData.getInt("maxBaseHealth");
		minBaseMana = jsonData.getInt("minBaseMana");
		maxBaseMana = jsonData.getInt("maxBaseMana");
		
		String[] typeStrings = jsonData.get("types").asStringArray();
		types = new ElementType[typeStrings.length];
		for(int i = 0; i < typeStrings.length; ++i) {
			types[i] = Enum.valueOf(ElementType.class, typeStrings[i]);
		}
		
		JsonValue compCost = jsonData.get("componentCost").child();
		while(compCost != null) {
			componentCost.add(new Pair<ElementType, Integer>(
					Enum.valueOf(ElementType.class, compCost.name),
					compCost.asInt()
			));
			compCost = compCost.next();
		}
		
	}
	
}
