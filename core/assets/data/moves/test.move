{
name: Toxify,
desc: Toxify,
accuracy: 1,
power: 10,
manaCost: 5,
elementType: AIR,
moveType: PHYSICAL,
actions: [
	{
		class: AilmentApplicator
		target: SOURCE
		chance: 1
		ailmentName: Toxic
		duration: 4
	},
]
priority: 1,
turnType: INSTANT
}