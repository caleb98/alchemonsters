{
	name: "Charged Fireball",
	desc: "Casts a fireball at the enemy.",
	accuracy: 1,
	manaCost: 5,
	critStage: 1,
	elementType: "FIRE",
	moveType: "MAGIC",
	actions: [
		{
			class: "Damage",
			target: "OPPONENT",
			power: 400
		}
	],
	turnType: "CHARGE",
	priority: 0
}