1.20.1-1.12
---

## Added

### Player Card

The player card allows the player to store a self-reference
to be used on the player interface or the ender chest interface

### Player Interface

The player interface allows a computer to access a player inventory
and also make the player do some actions, like looking at a coordinate,
and consuming a food item

All inventory operations are supported but the interface can't
be used as an inventory target

### Ender Chest Interface

The ender chest interface allows manipulation of a player's 
ender chest inventory

All inventory operations are supported but the interface can't
be used as an inventory target

### Ender Bag
A utility item that allows players to access theirs ender chest through 
the inventory

Turtles can equip the bag to access the turtle owner's ender chest

## Fixed

- Peripherals wouldn't drop themselves 

1.20.1-1.11
---

## Added

### Keyboards !

Keyboards can send keys from the client from any place close to
the computer the default range is 16 blocks from the computer but
it can be configurated up to 128 blocks

Players can still move the camera around and interact with monitors
and other blocks

## Changes

Magnetic cards are now dyeable 

## Fixed

- Random peripheral detach from the Magnetic Card Manipulator
- Anvil interface now returns an error if the slot that it will be
renamed doesn't have an item


1.20.1-1.10.1
---

## Fixed

Cable facades are now rendered as part of the cable model 

1.20.1-1.10
---

## Added

Cable facades, computercraft networking cables can now be hidden

## Fixed

Trading interfaces couldn't do trades