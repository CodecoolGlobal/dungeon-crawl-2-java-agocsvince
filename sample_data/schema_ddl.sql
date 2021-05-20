ALTER TABLE IF EXISTS ONLY public.enemy DROP CONSTRAINT IF EXISTS fk_player_id CASCADE;
ALTER TABLE IF EXISTS ONLY public.game_state DROP CONSTRAINT IF EXISTS fk_player_id CASCADE;


DROP TABLE IF EXISTS public.game_state;
CREATE TABLE public.game_state (
    id serial NOT NULL PRIMARY KEY,
    current_map text NOT NULL,
    saved_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    player_id text
);


DROP TABLE IF EXISTS public.player;
CREATE TABLE public.player (
    id text PRIMARY KEY,
    player_name text NOT NULL,
    hp integer NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    purse integer NOT NULL,
    item_hand_r integer NOT NULL,
    item_hand_l integer NOT NULL,
    item_head integer NOT NULL,
    item_chest integer NOT NULL
);

DROP TABLE IF EXISTS public.enemy;
CREATE TABLE public.enemy (
                               id serial NOT NULL PRIMARY KEY,
                               enemy_type text NOT NULL,
                               hp integer NOT NULL,
                               x integer NOT NULL,
                               y integer NOT NULL,
                               player_id text
);

ALTER TABLE ONLY public.game_state
    ADD CONSTRAINT fk_player_id FOREIGN KEY (player_id) REFERENCES public.player(id);
ALTER TABLE ONLY public.enemy
    ADD CONSTRAINT fk_player_id FOREIGN KEY (player_id) REFERENCES public.player(id);