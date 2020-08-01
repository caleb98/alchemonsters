POWER = 35
BUFFED_POWER = 70

isHit = moveInstance:rollHit()
isCrit = moveInstance:rollCrit()
isStab = moveInstance:checkStab()

if moveInstance.context.battleground.weather == WeatherType.DELUGE then
    POWER = BUFFED_POWER
end

if isHit then
	target = moveInstance.targets[1]
	
	damage = Damage:getDamageAgainst(moveInstance, target, POWER, isCrit, isStab)
	target:modifyHealth(-damage)
	Damage:publish(
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
end
