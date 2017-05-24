## PlayerShopsRPG - Region-based Player Shops

PlayerShopsRPG is a region-based player shop plugin, similar to the now-gone Bukkit plugin Command Shops.  Unlike it's predecessor however, PlayerShopsRPG fully supports customized items, including enchantments and lore.  In addition, the amount of commands required to interface with shops is kept to a minimum, instead most actions are performed by clicking on links in chat.  Shop types can also be specified to constrain the buying and selling of items to shops of a certain type.

## Rational
As the name implies PlayerShopsRPG is designed to be used with rpg-type servers where the clutter of physical shop objects (signs/chests) may be undesirable.  It was built as a compromise between the flexibility of command-based shops and the ease of use of GUI-based shops, relying primarily on clickable links in chat to perform most of it's functions.  It also allows server owners to create different types of shops, it doesn't make much sense for a Blacksmith to sell cake, does it?

## QuickStart - How to create your first shop
1. `/shop select` to select the region to create the shop
2. `/shop create My Wonderful QuickStart Shop` to create a shop named *My Wonderful QuickStart Shop*
3. Hold an item you want the shop to buy or sell in your hand and execute `/shop item create`
4. To stock your created item hold it in your hand and execute `/shop item add`
5. `/shop browse` to view the contents of your shop
6. ***Click*** on *Manager* to access the management page
7. ***Click*** on the first and second `--` to set the sell and buy price respectively.
8. **Profit!** 

## Dependencies
* Since PlayerShopRPG deals with currencies it requires that your server be running some implementation of the Sponge Economy API.

## Screenshots
**Browse View:**  
<img src="https://forums-cdn.spongepowered.org/uploads/default/original/2X/b/bfc486a76db91a99235a1db07a047da674e68d8c.png" width="690" height="388">
<img src="https://forums-cdn.spongepowered.org/uploads/default/original/2X/3/31f136ee5f836eee0acde0164bc62a07ad4d1695.png" width="690" height="388">

**Manager View:** (Can change the prices of items and well as stock and unstock items)  
<img src="https://forums-cdn.spongepowered.org/uploads/default/original/2X/d/de29fca6e4ba2b0970e8a031187c5b6426b5c5f7.png" width="690" height="388">

**Owner View:** (Can rename the shop, change the owner, deposit/withdraw shop funds, add/remove managers, and destroy items)  
<img src="https://forums-cdn.spongepowered.org/uploads/default/original/2X/2/2dda565013795548724f408a7c0547f80532a324.png" width="690" height="388">

## Links
* Be sure to check out the [PlayerShopsRPG wiki](https://github.com/Zerthick/PlayerShopsRPG/wiki) for useful guides and how-tos, I recommend you start with the [QuickStart Guide](https://github.com/Zerthick/PlayerShopsRPG/wiki/QuickStart)!

## Disclaimer
PlayerShopsRPG is still under heavy development, there may be a few bugs here and there.  If you find any be sure to create an [issue](https://github.com/Zerthick/PlayerShopsRPG/issues)!

## Suggestions
If you have any feedback or suggestions for PlayerShopsRPG I'd love to hear them! Have a cool new idea for a click-action?  Want a new feature added?  Let me know! :grinning:
 
## Support Me
I will **never** charge money for the use of my plugins, however they do require a significant amount of work to maintain and update. If you'd like to show your support and buy me a cup of tea sometime (I don't drink that horrid coffee stuff :P) you can do so [here](https://www.paypal.me/zerthick)
