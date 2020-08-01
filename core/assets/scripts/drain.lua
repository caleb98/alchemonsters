POWER = 40

isHit = moveInstance:rollHit()
isCrit = moveInstance:rollCrit()
isStab = moveInstance:checkStab()

if isHit then
	target = moveInstance.targets[1]
	
	--Deal damage
	damage = Damage:getDamageAgainst(moveInstance, target, POWER, isCrit, isStab)
	target:modifyHealth(-damage)
	Publisher:publish(
		luajava.newInstance(
			MCombatDamageDealt,
			moveInstance.context,
			moveInstance.source,
			target,
			moveInstance.move.name,
			moveInstance:getElementType(),
			damage,
			isCrit,
			isStab,
			false
		)
	)

	--Heal for half.
	healing = damage / 2
	moveInstance.source:modifyHealth(healing)
	Publisher:publish(
		luajava.newInstance(
			MCombatHealingReceived, 
			moveInstance.context, 
			moveInstance.source, 
			moveInstance.source, 
			"Drain", 
			healing, 
			false
		)
	)
end
