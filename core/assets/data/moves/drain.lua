POWER = 40

isHit = Damage:rollHit(move, context, source, target)
isCrit = Damage:rollCrit(move, context, source, target)
isStab = Damage:checkStab(move, context, source, target)

if isHit then
	damage = Damage:getDamageAgainst(move, context, source, target, POWER, isCrit, isStab)
	target.currentHealth = target.currentHealth - damage
	Damage:publish(luajava.newInstance(MCombatDamageDealt, context, source, target, move.name, move.elementType, damage, isHit, isCrit, false))
	
	healing = damage / 2
	source.currentHealth = source.currentHealth + healing
	if source.currentHealth > source.maxHealth then
		source.currentHealth = source.maxHealth
	end
	Damage:publish(luajava.newInstance(MCombatHealingReceived, context, source, source, "Drain", healing, false))
end
