/*
 *	CREATE THE TABLE USED TO STORE
 *  CREATURE BASES.
 */
CREATE TABLE CreatureBases (
	ID INTEGER PRIMARY KEY,
	CreatureName TEXT NOT NULL,
	Description TEXT NOT NULL,
	MinBaseHealth INTEGER NOT NULL, 
	MaxBaseHealth INTEGER NOT NULL,
	MinBaseMana INTEGER NOT NULL,
	MaxBaseMana INTEGER NOT NULL,
	BaseVitae INTEGER NOT NULL,
	BaseFocus INTEGER NOT NULL,
	BaseMagicAttack INTEGER NOT NULL,
	BaseMagicDefense INTEGER NOT NULL,
	BasePhysicalAttack INTEGER NOT NULL,
	BasePhysicalDefense INTEGER NOT NULL,
	BasePenetration INTEGER NOT NULL,
	BaseResistance INTEGER NOT NULL,
	BaseSpeed INTEGER NOT NULL,
	TypeOne INTEGER NOT NULL REFERENCES ElementTypes(ID),
	TypeTwo INTEGER REFERENCES ElementTypes(ID),
	Tier INTEGER NOT NULL
);

/*
 *	CREATE THE MOVE TABLE.
 */
CREATE TABLE Moves (
	ID INTEGER PRIMARY KEY,
	MoveName TEXT NOT NULL,
	Description TEXT NOT NULL,
	Accuracy REAL NOT NULL,
	ManaCost INTEGER NOT NULL,
	CritStage INTEGER NOT NULL,
	ElementType INTEGER NOT NULL REFERENCES ElementTypes(ID),
	MoveType INTEGER NOT NULL REFERENCES MoveTypes(ID),
	TurnType INTEGER NOT NULL REFERENCES TurnTypes(ID),
	TargetSelectType INTEGER NOT NULL REFERENCES TargetSelectTypes(ID),
	Priority INTEGER NOT NULL,
	DelayAmount INTEGER NOT NULL,
	ActionDefinition TEXT NOT NULL
);

/*
 *	CREATE THE MOVE TAGS TABLE.
 */
CREATE TABLE MoveTags (
	ID INTEGER NOT NULL REFERENCES Moves(ID),
	Tag TEXT NOT NULL,
	CONSTRAINT PKTag PRIMARY KEY (ID, Tag)
);

/*
 *	CREATE THE TYPE CHART TABLE.
 */
CREATE TABLE ElementTypes (
	ID INTEGER NOT NULL PRIMARY KEY,
	Type TEXT NOT NULL
);

INSERT INTO ElementTypes VALUES
(1, 'AIR'),
(2, 'FEY'),
(3, 'FIRE'),
(4, 'GROUND'),
(5, 'LIGHTNING'),
(6, 'UNDEAD'),
(7, 'VOID'),
(8, 'WATER'),
(9, 'ARCANUM'),
(10, 'SYNTHETIC');

/*
 *	CREATE MOVE TYPES TABLE.
 */
CREATE TABLE MoveTypes (
	ID INTEGER NOT NULL PRIMARY KEY,
	Type TEXT NOT NULL
);

INSERT INTO MoveTypes VALUES
(1, 'MAGIC'),
(2, 'PHYSICAL'),
(3, 'NEUTRAL');

/*
 *	CREATE TURN TYPES TABLE.
 */
CREATE TABLE TurnTypes (
	ID INTEGER NOT NULL PRIMARY KEY,
	Type TEXT NOT NULL
);

INSERT INTO TurnTypes VALUES
(1, 'INSTANT'),
(2, 'CHARGE'),
(3, 'RECHARGE'),
(4, 'DELAYED');

/*
 *	CREATE TARGET SELECT TYPES TABLE.
 */
CREATE TABLE TargetSelectTypes (
	ID INTEGER NOT NULL PRIMARY KEY,
	Type TEXT NOT NULL
);

INSERT INTO TargetSelectTypes VALUES
(1, 'SINGLE_OPPONENT'),
(2, 'OPPONENT_TEAM'),
(3, 'SINGLE_FRIENDLY'),
(4, 'FRIENDLY_TEAM'),
(5, 'SELF'),
(6, 'NONE'),
(7, 'SINGLE_ANY'),
(8, 'ALL');

/*
 *	INSERT CREATURE DEFINITIONS.
 */
INSERT INTO CreatureBases 
(CreatureName, Description, 
MinBaseHealth, MaxBaseHealth, 
MinBaseMana, MaxBaseMana,
BaseVitae, BaseFocus, 
BaseMagicAttack, BaseMagicDefense, 
BasePhysicalAttack, BasePhysicalDefense,
BasePenetration, BaseResistance, BaseSpeed,
TypeOne, TypeTwo, Tier)
VALUES

--AEROCH
(
'Aeroch', 
'What most people would call a chick, the Aeroch often has a 
parasitic relationship to mother birds, hiding in their nests 
and stealing food.',
13, 19, 19, 22, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--ALBATORRENT
(
'Albatorrent', 
'Albatorrents are adept anglers, using their wings made of water 
to corral fish into a place where they can eat them easier. These 
large birds are known for their ability to fly long distances 
without landing on solid ground.',
32, 36, 20, 25, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
8, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--AQUEELECTRIX
(
'Aqueelectrix', 
'A speedy aquatic creature, the Aqueelectrix chases down its 
prey and releases a high voltage shock to subdue it.',
25, 30, 27, 31, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
5, --TypeOne (Primary)
8, --TypeTwo (Secondary)
1
),

--AXLIMUK
(
'Axlimuk', 
'The frills on the head of an Axlimuk allow for it to breath 
even when completely submerged, as they act like gills. These 
appendages also help them to dig through the mud where they 
find their food.',
30, 36, 22, 25, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
4, --TypeOne (Primary)
8, --TypeTwo (Secondary)
1
),

--BELDAVRE
(
'Beldavre', 
'A soul linked to the ocean, Beldavre consist of dark bubbles 
floating into a bulbous helmet, remniscient of a diver''s suit. 
Rarely encountered by humans, the motives of Beldavre largely 
remain a mystery.',
32, 39, 20, 22, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
8, --TypeTwo (Secondary)
1
),

--CAVERNUS
(
'Cavernus', 
'A gaping mouth that leads to an enveloping darkness is the 
first thing that explorers see when facing a Cavernus. Extremely 
hostile creatures, Cavernus''s use their rocky teeth to bite 
anything that invades their territory.',
33, 39,19, 22, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
4, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--CERBURROST
(
'Cerburrost', 
'The ability to quickly reduce temperatures makes Cerburrost a 
popular companion around the world. Its loyalty is not found in 
many ohter creatures. Cerburrost are easy to distinguish - 
especially at night - due to their glowing blue eyes.',
32, 35, 20, 26, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
3, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--CHARKITE
(
'Charkite', 
'A bird born of fire, Charkite often gather around heat sources. 
If they find a place warm enough, they can forgo eating for days, 
instead absorbing heat as a source of energy. These bright red 
birds can breath fire and ignite themselves as a defense mechanism 
when in extreme danger. Looking beneath a Charkite''s feathers 
reveals a waxy substance covering it''s skin.',
12, 315, 41, 46, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
3, --TypeTwo (Secondary)
1
),

--CONDUCTIVOLT
(
'Conductivolt', 
'People who don''t understand lightning may tell you it never 
strikes the same place twice, but Conductivolt will prove them 
wrong. Powered from the electricity generated from storms, 
Conductivolts are often sighted near the first sounds of thunder 
trying to use their metallic hown to absorb the incoming bolts of 
lightning.',
24, 27, 28, 34, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
5, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--DJINNDROCK
(
'Djinndrock', 
'A wanderer of the sands, Djinndrock often cross the paths of 
weary travellers. Usually they will help them in small ways, but 
if you ask too much, they will take offense and attack.',
34, 40, 18, 21, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
4, --TypeOne (Primary)
2, --TypeTwo (Secondary)
1
),

--ECTOVIRE
(
'Ectovire', 
'Many are in agreement that Ectovire are born from those who 
perish during storms. From a distance, these beings look like 
glowing orbs and often lead travelling ships to safety through 
stormy weather.',
19, 20, 33, 41, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
5, --TypeOne (Primary)
6, --TypeTwo (Secondary)
1
),

--FEYRITE
(
'Feyrite', 
'Known worldwide as a quiet observer Feyrite seem to being 
capable of rational thought to some degreee, recording what
they see in an ever-changing language.',
10, 12, 27, 29, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--GHODMA
(
'Ghodama', 
'Helpful souls, the Ghodama are often called Wayfinders of the 
Forest as they guide lost travelers through the dense woodlands.',
14, 15, 18, 26, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
6, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--GOBLAZE
(
'Goblaze', 
'Very few living things don''t have an inate fear of fire. Even 
fewer are attracted to it. Goblazes are what many would call 
pyromaniacs. They are obsessed with it, seemingly able to 
conjure it at will. If you see a Goblaze without a fire nearby, 
you can tell something is bothering it.',
23, 24, 29, 37, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
3, --TypeTwo (Secondary)
1
),

--GUBBLE
(
'Gubble', 
'A small fish that can use its large mouth to disrupt small swimming 
creatures.',
16, 21, 16, 20, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
8, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--LAVION
(
'Lavion', 
'Lavion is a large feline. Males are crested with a flowing mane of 
lava while females have sharp shards of rock along their back. The male''s 
mane helps to guide the females back home after a hunt.',
21, 25, 31, 36, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
3, --TypeOne (Primary)
4, --TypeTwo (Secondary)
1
),

--MERMAFLO
(
'Mermaflo', 
'Small creatures with features of both humans and fish, the 
Mermaflo are often found building small structures in the coral 
reefs outside of the main land. No one knows how the structures 
are used, but they seem to amplify the Mermaflo''s calls.',
16, 21, 36, 40, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
8, --TypeTwo (Secondary)
1
),

--MUMMARBLE
(
'Mummarble', 
'Deserts are inhospitable places, but those who make their 
eternal home there are destined to become one with it. This 
happens through a transformation that creates a Mummarble, a 
humanoid figure wreathed in sand. Behind the sand is a totemic 
pillar of marble with faces engraved into it. These faces are 
said to be the faces of the spirits empowering it.',
37, 42, 15, 19, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
4, --TypeOne (Primary)
6, --TypeTwo (Secondary)
1
),

--NOBYSS
(
'Nobyss', 
'Nobyss is the source of much interest. No one has been able to 
determine what this wispy creature eats, leading many to believe 
it is powered by some form of magic.',
16, 20, 16, 21, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--OPRIXIE
(
'Oprixie', 
'The echoing sounds of singing is usually a welcome sign to any 
from the region, as it means Oprixie have taken residence nearby. 
Oprixie are brilliant musicians and often attract tourists from 
neighboring cities.',
13, 16, 38, 45, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
2, --TypeTwo (Secondary)
1
),

--PEBLITH
(
'Peblith', 
'A minute organism that resembles a small rock. It is often 
compared to a rodent because of its large front teeth.',
24, 30, 8, 11, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
4, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--SANDORNO
(
'Sandorno', 
'Often in the deserts beneath the grand mountains, winds will 
whip up into spinning masses. Sometimes, this is a phenomenon 
causes by the weather, while other times it is caused by a 
Sandorno. Sandorno are large flightless birds that use their 
speed to capture their prey within sand walls. Legends says that 
if a Sandorno ever stops running, it will perish.',
24, 30, 26, 31, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
4, --TypeTwo (Secondary)
1
),

--SARVACUOGO
(
'Sarvacuoga', 
'A haunting space, the opening of a casket is. But if you see 
one with teeth, watch out! It''s most likely a Sarvacuogo. 
Sarvacuogo appear in urban areas, hungry for spirits to feed 
them and attacking anyone who approaches them in the catacombs 
with disembodied hands.',
29, 34, 23, 27, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
7, --TypeOne (Primary)
6, --TypeTwo (Secondary)
1
),

--SCORCRUB
(
'Scorcrub', 
'A playful feline-like creature with specialized glands used to 
produce intense heat.',
16, 18, 16, 23, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
3, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--SHOXABLAZE
(
'Shoxablaze', 
'A sturdy beast with horns that spark with electricity. Defensive 
and territorial, anyone within sight of a Shoxablaze should expect 
an attack. Even if you''re agile enough to dodge the initial charge, 
be sure to watch out for the flaming tail as well!',
26, 29, 26, 32, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
3, --TypeOne (Primary)
5, --TypeTwo (Secondary)
1
),

--SIRELENCE
(
'Sirelence', 
'Alchemists have studied the Sirelence for ages. They have the 
ability to scream in such a way that it causes complete silence, 
a feat that baffles even the brightest researchers. Their appearance 
is usually comparable to that of a woman in most ways, except these 
creatures have an extremely wide mouth.',
23, 26, 29, 35, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--SPIRIGNIT
(
'Spirignit', 
'In ages far past, those who saw flames within their fireplace take 
the form of a being were outcast and ridiculed. In recent times, however, 
people have learned that these forms, called Spirignits, are vengeful 
spirits empowered by the flames they inhabit.',
29, 35, 23, 26, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
3, --TypeOne (Primary)
6, --TypeTwo (Secondary)
1
),

--THUNDYVERN
(
'Thundyvern', 
'The sound of thunder crashes above, yet no lighning is seen. 
Birds soon scatter and the air feels charged with energy. At 
this point, it is time to prepare for battle; a Thundyvern is 
near and there is no running from it.',
26, 28, 26, 33, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
5, --TypeTwo (Secondary)
1
),

--VOLTHARE
(
'Volthare', 
'A rabbit that lives life in the fast lane. Volthare is as 
speedy as they come, often found racing others of its kind 
until it passes out from exhaustion.',
10, 16, 22, 25, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
5, --TypeOne (Primary)
NULL, --TypeTwo (Secondary)
1
),

--WHALEWHIRL
(
'Whalewhirl', 
'Described by scholars as a creature with a bottomless stomach, 
Whalewhirls are usually non-hostile. They prey upon small 
creatures that fill the oceans by opening their enormous mouths and 
engulfing large volumes of water.',
43, 50, 9, 11, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
8, --TypeOne (Primary)
7, --TypeTwo (Secondary)
1
),

--ZAPROGGL
(
'Zaproggl', 
'A furry little creature with the ability to open up small portals, 
the Zaproggl is quite an annoyance. While it uses these portals to 
safely pick fruits from trees, it has another use for them: irritating 
humans. If you ever receive a nasty shock while walking along the 
roadways, you can assume a Zaproggl is nearby taking pleasure in your 
anger.',
14, 16, 38, 45, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
2, --TypeOne (Primary)
5, --TypeTwo (Secondary)
1
),

--ZEPHYRAITH
(
'Zephyraith', 
'The voice you hear in the wind? A Zephyraith. These ghoulish 
creatures resemble incorporeal birds and are often quite hostile. 
They attack by generating vicious winds with their ghostly wings, 
often pinning their target against trees.',
20, 23, 32, 38, --MinBaseHealth, MaxBaseHealth, MinBaseMana, MaxBaseMana
200, 200, --BaseVitae, BaseFocus
200, 200, --BaseMagicAtk, BaseMagicDef
200, 200, --BasePhysAtk, BasePhysDef
200, 200, 200, --BasePen, BaseRes, BaseSpeed
1, --TypeOne (Primary)
6, --TypeTwo (Secondary)
1
);

/*
 *	INSERT MOVE DEFINITIONS.
 */
INSERT INTO Moves 
(MoveName, Description,
Accuracy, ManaCost, CritStage,
ElementType, MoveType, TurnType, TargetSelectType,
Priority, DelayAmount,
ActionDefinition)
VALUES

--BLACK HOLE
(
'Black Hole', --Name
'<TODO DESCRIPTION>', --Description
1.00, 5, 1, --Accuracy, ManaCost, CritStage
7, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'blackhole.json' --ActionDefinition (name of move definition file)
),

--DRAIN
(
'Drain', --Name
'<TODO DESCRIPTION>', --Description
0.85, 1, 1, --Accuracy, ManaCost, CritStage
7, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'drain.json' --ActionDefinition (name of move definition file)
),

--ELECTRON BARRAGE
(
'Electron Barrage', --Name
'<TODO DESCRIPTION>', --Description
1.00, 5, 3, --Accuracy, ManaCost, CritStage
5, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'electronbarrage.json' --ActionDefinition (name of move definition file)
),

--EMBER SPRAY
(
'Ember Spray', --Name
'<TODO DESCRIPTION>', --Description
1.00, 10, 1, --Accuracy, ManaCost, CritStage
3, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
1, 0, --Priority, DelayAmount
'emberspray.json' --ActionDefinition (name of move definition file)
),

--PEBBLE THROW
(
'Pebble Throw', --Name
'<TODO DESCRIPTION>', --Description
1.00, 1, 1, --Accuracy, ManaCost, CritStage
4, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'pebblethrow.json' --ActionDefinition (name of move definition file)
),

--PYROCLASM
(
'Pyroclasm', --Name
'<TODO DESCRIPTION>', --Description
1.00, 20, 1, --Accuracy, ManaCost, CritStage
3, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'pyroclasm.json' --ActionDefinition (name of move definition file)
),

--SPARK
(
'Spark', --Name
'<TODO DESCRIPTION>', --Description
1.00, 1, 2, --Accuracy, ManaCost, CritStage
5, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'spark.json' --ActionDefinition (name of move definition file)
),

--SPLASH
(
'Splash', --Name
'<TODO DESCRIPTION>', --Description
1.00, 1, 1, --Accuracy, ManaCost, CritStage
8, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'splash.json' --ActionDefinition (name of move definition file)
),

--TECTONIC ASSAULT
(
'Tectonic Assault', --Name
'<TODO DESCRIPTION>', --Description
1.00, 10, 1, --Accuracy, ManaCost, CritStage
4, 2, 3, 1, --ElementType, MoveType, TurnType, TargetSelectType
-1, 0, --Priority, DelayAmount
'tectonicassault.json' --ActionDefinition (name of move definition file)
),

--DREAM SCREAM
(
'Dream Scream', --Name
'<TODO DESCRIPTION>', --Description
1.00, 20, 1, --Accuracy, ManaCost, CritStage
2, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'dreamscream.json' --ActionDefinition (name of move definition file)
),

--STATIC CHARGE
(
'Static Charge', --Name
'<TODO DESCRIPTION>', --Description
1.00, 5, 0, --Accuracy, ManaCost, CritStage
5, 3, 1, 5, --ElementType, MoveType, TurnType, TargetSelectType
1, 0, --Priority, DelayAmount
'staticcharge.json' --ActionDefinition (name of move definition file)
),

--BOLT RUSH
(
'Bolt Rush', --Name
'<TODO DESCRIPTION>', --Description
1.00, 15, 3, --Accuracy, ManaCost, CritStage
5, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'boltrush.json' --ActionDefinition (name of move definition file)
),

--SWARM BURST
(
'Swarm Burst', --Name
'<TODO DESCRIPTION>', --Description
0.90, 15, 1, --Accuracy, ManaCost, CritStage
6, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'swarmburst.json' --ActionDefinition (name of move definition file)
),

--FESTERING SWEEP
(
'Festering Sweep', --Name
'<TODO DESCRIPTION>', --Description
1.00, 25, 1, --Accuracy, ManaCost, CritStage
6, 2, 1, 2, --ElementType, MoveType, TurnType, TargetSelectType
-2, 0, --Priority, DelayAmount
'festeringsweep.json' --ActionDefinition (name of move definition file)
),

--UPDRAFT
(
'Updraft', --Name
'<TODO DESCRIPTION>', --Description
1.00, 1, 1, --Accuracy, ManaCost, CritStage
1, 1, 1, 2, --ElementType, MoveType, TurnType, TargetSelectType
1, 0, --Priority, DelayAmount
'updraft.json' --ActionDefinition (name of move definition file)
),

--BILE BOMB
(
'Bile Bomb', --Name
'<TODO DESCRIPTION>', --Description
1.00, 20, 1, --Accuracy, ManaCost, CritStage
2, 1, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'bilebomb.json' --ActionDefinition (name of move definition file)
),

--BLADE WING
(
'Blade Wing', --Name
'<TODO DESCRIPTION>', --Description
1.00, 5, 1, --Accuracy, ManaCost, CritStage
1, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'bladewing.json' --ActionDefinition (name of move definition file)
),

--AQUATIC BLITZ
(
'Aquatic Blitz', --Name
'<TODO DESCRIPTION>', --Description
0.95, 15, 1, --Accuracy, ManaCost, CritStage
8, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
1, 0, --Priority, DelayAmount
'aquaticblitz.json' --ActionDefinition (name of move definition file)
),

--VEILING SANDS
(
'Veiling Sands', --Name
'<TODO DESCRIPTION>', --Description
1.00, 10, 0, --Accuracy, ManaCost, CritStage
4, 3, 1, 5, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'veilingsands.json' --ActionDefinition (name of move definition file)
),

--TUMBLING STONES
(
'Tumbling Stones', --Name
'<TODO DESCRIPTION>', --Description
1.00, 15, 1, --Accuracy, ManaCost, CritStage
4, 2, 1, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'tumblingstones.json' --ActionDefinition (name of move definition file)
),

--PRECIPITATE
(
'Precipitate', --Name
'<TODO DESCRIPTION>', --Description
1.00, 5, 0, --Accuracy, ManaCost, CritStage
8, 3, 1, 6, --ElementType, MoveType, TurnType, TargetSelectType
2, 0, --Priority, DelayAmount
'precipitate.json' --ActionDefinition (name of move definition file)
),

--STORM SURGE
(
'Storm Surge', --Name
'<TODO DESCRIPTION>', --Description
1.00, 15, 1, --Accuracy, ManaCost, CritStage
8, 2, 2, 1, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'stormsurge.json' --ActionDefinition (name of move definition file)
),

--TESLA STORM
(
'Tesla Storm', --Name
'<TODO DESCRIPTION>', --Description
1.00, 25, 3, --Accuracy, ManaCost, CritStage
5, 1, 1, 2, --ElementType, MoveType, TurnType, TargetSelectType
0, 0, --Priority, DelayAmount
'teslastorm.json' --ActionDefinition (name of move definition file)
);

.save core/assets/gamedata.db
.exit



























