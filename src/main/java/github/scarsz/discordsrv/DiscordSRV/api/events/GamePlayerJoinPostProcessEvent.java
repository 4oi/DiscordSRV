package github.scarsz.discordsrv.DiscordSRV.api.events;

/**
 * Made by Scarsz
 *
 * @in /dev/hell
 * @on 11/25/2016
 * @at 10:16 PM
 */
public class GamePlayerJoinPostProcessEvent extends GamePlayerJoinEvent {

    private String player = null;

    public GamePlayerJoinPostProcessEvent(String player) {
        this.player = player;
    }

    @Override
    public String getPlayer() {
        return player;
    }
    @Override
    public void setPlayer(String player) {
        this.player = player;
    }

    //TODO
    
}
