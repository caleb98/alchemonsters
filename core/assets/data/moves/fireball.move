{
	name: "Fireball",
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
			power: 5
		},
		{
			class: "Damage",
			target: "OPPONENT",
			power: 5
		},
		{
			class: "Damage",
			target: "OPPONENT",
			power: 5
		}
	],
	turnType: "INSTANT",
	priority: 0
}