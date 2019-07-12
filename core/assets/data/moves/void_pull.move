{
	name: "Void Pull",
	desc: "Whenever the target takes damage, they take an additional 25% of that damage as void type",
	accuracy: 1,
	power: 10,
	manaCost: 5,
	critStage: 1,
	elementType: "FIRE",
	moveType: "MAGIC",
	actions: [
		{
			class: "AilmentApplicator",
			target: "OPPONENT",
			ailmentName: "Void Pull",
			chance: 1
		}
	],
	turnType: "INSTANT",
	priority: 0
}