{
	name: "Strength of Flame",
	desc: "Empowers with the strength of flame.",
	accuracy: 1,
	power: 10,
	manaCost: 5,
	critStage: 1,
	elementType: "FIRE",
	moveType: "MAGIC",
	actions: [
		{
			class: "StatModifier",
			target: "SELF",
			stat: "MAGIC_POWER",
			amt: 6
		}
	],
	turnType: "INSTANT",
	priority: 0
}