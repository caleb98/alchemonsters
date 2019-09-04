POWER = 35
BUFFED_POWER = 70

isHit = Damage:rollHit(move, context, source, target)
isCrit = Damage:rollCrit(move, context, source, target)
isStab = Damage:checkStab(move, context, source, target)

if context.battleground.weather == WeatherType.DELUGE then
	POWER = 70
end

damage = Damage:getDamageAgainst(move, context, source, target, POWER, isCrit, isStab)
target.currentHealth = target.currentHealth - damage
Damage:publish(luajava.newInstance(MCombatDamageDealt, context, source, target, move.name, move.elementType, damage, isHit, isCrit, false))
