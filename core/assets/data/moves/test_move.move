{
	name: "TEST_MOVE",
	desc: "MOVE FOR TESTING PURPOSES",
	accuracy: 1,
	manaCost: 0,
	critStage: 1,
	elementType: "VOID",
	moveType: "MAGIC",
	actions: [
		{
			class: "Combine",
			actions: [
				{
					class: "Script",
					script: "print('action 1')"
				},
				{
					class: "Script",
					script: "print('action 2')"
				}
			]
		}
	],
	turnType: "INSTANT",
	priority: 0
}