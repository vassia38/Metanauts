--
-- PostgreSQL database dump
--

-- Dumped from database version 14.0
-- Dumped by pg_dump version 14.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: socialnetwork; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE socialnetwork WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'Romanian_Romania.1250';


ALTER DATABASE socialnetwork OWNER TO postgres;

\connect socialnetwork

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: friendships; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friendships (
    id1 integer,
    id2 integer,
    date_friendship character varying NOT NULL
);


ALTER TABLE public.friendships OWNER TO postgres;

--
-- Name: group_members; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.group_members (
    id_group integer NOT NULL,
    id_user integer NOT NULL
);


ALTER TABLE public.group_members OWNER TO postgres;

--
-- Name: group_messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.group_messages (
    id integer NOT NULL,
    source_id integer NOT NULL,
    message_text character varying NOT NULL,
    date character varying NOT NULL,
    replied_message_id integer,
    id_group integer NOT NULL
);


ALTER TABLE public.group_messages OWNER TO postgres;

--
-- Name: group_messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.group_messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.group_messages_id_seq OWNER TO postgres;

--
-- Name: group_messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.group_messages_id_seq OWNED BY public.group_messages.id;


--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
    id_group integer NOT NULL,
    name_group character varying NOT NULL
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    id integer NOT NULL,
    source_id integer NOT NULL,
    message_text character varying NOT NULL,
    date character varying NOT NULL,
    replied_message_id integer
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: messages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.messages_id_seq OWNER TO postgres;

--
-- Name: messages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messages_id_seq OWNED BY public.messages.id;


--
-- Name: requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.requests (
    id1 integer NOT NULL,
    id2 integer NOT NULL,
    status character varying NOT NULL,
    "dateSent" character varying
);


ALTER TABLE public.requests OWNER TO postgres;

--
-- Name: socialevents; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.socialevents (
    id integer NOT NULL,
    name character varying NOT NULL,
    date character varying NOT NULL,
    coverphoto character varying,
    id_host integer NOT NULL
);


ALTER TABLE public.socialevents OWNER TO postgres;

--
-- Name: socialevents_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.socialevents_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.socialevents_id_seq OWNER TO postgres;

--
-- Name: socialevents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.socialevents_id_seq OWNED BY public.socialevents.id;


--
-- Name: socialevents_participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.socialevents_participants (
    id integer NOT NULL,
    id_user integer NOT NULL,
    notification integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.socialevents_participants OWNER TO postgres;

--
-- Name: source_destination; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.source_destination (
    source_id integer NOT NULL,
    destination_id integer NOT NULL,
    message_id integer NOT NULL
);


ALTER TABLE public.source_destination OWNER TO postgres;

--
-- Name: table_name_id_group_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.table_name_id_group_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.table_name_id_group_seq OWNER TO postgres;

--
-- Name: table_name_id_group_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.table_name_id_group_seq OWNED BY public.groups.id_group;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying NOT NULL,
    first_name character varying NOT NULL,
    last_name character varying NOT NULL,
    user_password character varying NOT NULL,
    salt character varying NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: group_messages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages ALTER COLUMN id SET DEFAULT nextval('public.group_messages_id_seq'::regclass);


--
-- Name: groups id_group; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups ALTER COLUMN id_group SET DEFAULT nextval('public.table_name_id_group_seq'::regclass);


--
-- Name: messages id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages ALTER COLUMN id SET DEFAULT nextval('public.messages_id_seq'::regclass);


--
-- Name: socialevents id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents ALTER COLUMN id SET DEFAULT nextval('public.socialevents_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: friendships; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friendships (id1, id2, date_friendship) FROM stdin;
23	26	2022-01-14T11:24:38.019041600
18	19	2022-01-14T11:35:12.891925900
18	20	2022-01-14T11:37:22.006585100
19	20	2022-01-14T11:37:23.800813100
21	22	2022-01-14T11:55:18.219934600
21	26	2022-01-14T13:01:35.303851600
18	24	2022-01-15T11:13:56.700279400
\.


--
-- Data for Name: group_members; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.group_members (id_group, id_user) FROM stdin;
22	21
22	22
22	26
23	18
23	19
23	20
24	18
25	18
25	20
\.


--
-- Data for Name: group_messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.group_messages (id, source_id, message_text, date, replied_message_id, id_group) FROM stdin;
20	21	ciao ciao	2022-01-14T13:02:09.410997700	\N	22
21	18	hello grupa	2022-01-14T15:49:43.900798100	\N	23
22	19	hellooooooo	2022-01-14T15:50:14.188686700	21	23
23	18	aasd	2022-01-14T15:50:18.348340900	\N	23
24	18	bbbb	2022-01-14T23:44:07.362406500	23	23
25	18	ccc	2022-01-14T23:44:10.018764300	24	23
\.


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groups (id_group, name_group) FROM stdin;
22	gigi's friends
23	grupa 224
24	a
25	asdf
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (id, source_id, message_text, date, replied_message_id) FROM stdin;
45	18	aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa	2022-01-14T13:21:55.806259700	\N
46	18	replying	2022-01-14T13:52:52.663141800	44
47	18	self reply	2022-01-14T13:53:58.654808100	46
48	18	a	2022-01-14T15:04:31.455164500	\N
49	18	b	2022-01-14T15:41:46.878735800	\N
50	18	Buna seara cf	2022-01-14T20:07:04.250919200	\N
51	19	ciao ciao	2022-01-14T23:34:15.764589400	\N
52	19	aaaa	2022-01-14T23:34:17.300077400	\N
53	19	bawe	2022-01-14T23:34:18.980767400	\N
54	19	bai cf	2022-01-14T23:34:22.932635600	\N
55	19	helen, me, is replying	2022-01-14T23:36:24.339777600	49
56	19	aaa	2022-01-14T23:36:54.331930800	44
57	18	c	2022-01-14T23:40:34.802978500	\N
58	18	b	2022-01-14T23:40:36.938827700	57
59	18	d	2022-01-14T23:40:41.875443500	58
60	18	another msg	2022-01-15T00:26:23.672539300	\N
61	18	msgggg	2022-01-15T00:26:26.904372700	\N
62	18	buzzz	2022-01-15T00:26:28.816333800	\N
63	18	buzz	2022-01-15T00:26:30.760718300	\N
64	18	BUZZZZZ	2022-01-15T00:26:33.728556800	\N
65	18	:D	2022-01-15T00:26:36.856672300	\N
66	18	asdf	2022-01-15T11:12:13.768518	56
42	19	bunaaa	2022-01-14T11:35:37.870535200	\N
43	19	looooooooooooooooooooooooooooong	2022-01-14T11:35:43.149986700	\N
44	19	EVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEN LOOOOOOOOOOOOOOOOOOOOONGEEEEEEEEEEEEEEEEEER MESSAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGE	2022-01-14T11:35:59.309995100	\N
\.


--
-- Data for Name: requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.requests (id1, id2, status, "dateSent") FROM stdin;
24	18	approve	2022-01-15T10:44:24.317173200
26	23	approve	2022-01-14T11:23:45.360613400
18	19	approve	2022-01-14T11:33:31.150302700
18	20	approve	2022-01-14T11:33:51.646744600
19	20	approve	2022-01-14T11:36:58.757652100
22	21	approve	2022-01-14T11:54:34.673886700
20	23	pending	2022-01-14T11:37:29.205432400
23	21	reject	2022-01-14T12:42:58.886863600
26	21	approve	2022-01-14T13:01:05.242908300
24	25	reject	2022-01-14T11:30:29.127115200
24	19	pending	2022-01-15T10:43:43.340973400
25	18	reject	2022-01-15T10:44:58.052782100
\.


--
-- Data for Name: socialevents; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.socialevents (id, name, date, coverphoto, id_host) FROM stdin;
1	ASTROWORLD	2022-01-16T20:00		18
3	Revelion la oache	2022-12-31T00:00		18
4	lma	2022-03-01T08:00		18
6	my bday	2022-03-01T00:00		19
7	new event	2022-02-01T13:00		18
\.


--
-- Data for Name: socialevents_participants; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.socialevents_participants (id, id_user, notification) FROM stdin;
1	18	1
3	18	1
4	18	1
6	19	1
4	20	1
7	18	1
6	18	0
\.


--
-- Data for Name: source_destination; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.source_destination (source_id, destination_id, message_id) FROM stdin;
18	19	45
18	19	46
18	19	47
18	19	48
18	19	49
18	20	50
19	20	51
19	20	52
19	20	53
19	20	54
19	18	55
19	18	56
18	19	57
18	19	58
18	19	59
18	20	60
18	20	61
18	20	62
18	20	63
18	20	64
18	20	65
18	19	66
19	18	42
19	18	43
19	18	44
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, first_name, last_name, user_password, salt) FROM stdin;
18	vassco	Vasi	Ifrim	1290d9ee14030ef813d48eba9610d587c3229393c665192936fb0989ba42f934	1469380150
19	helen	Elena	Isaia	768651d625a41e831f7e1004320e1cd60ac734093c6e1a87fed38d97ad6de5d	1307076454
20	marco_t	Marco	Traian	865c5b7ff5b40e2b7b3dc691af0d184fec410433493e2282e51e06435b5b309f	-1622663087
21	gigi	Gigi	Becks	90981eee7d38d93593c973feea7b2d7034170fc32795414d4e71443d8f070067	1947690285
22	optimus	Optimus	Prime	4d48ae269b8cd7be27d3a33c95abc0a89c324b57f7fd41cf2a26b050d3254e0c	1995406627
23	hackerman	Hacker	Man	2995bc316e3e16e23cde4b66b16e533c438570304f6331a791e0c60300ab19cc	270006605
24	nihilus	Darth	Nihilus	c47878b486411193eb3c2dd52831279183324de02cee3d626cd15b414e913661	1757519287
25	revan	Darth	Revan	581cd5d49eff14be8fc4639b88890fd005cfe62aa5cbca49a77949aab0f9b687	-228816543
26	marmota	Marmo	Mota	1e72712959ab25a85a4933b7cb0d8fbf2933564c5d639a14d990f1f1bc8a85a4	473507558
\.


--
-- Name: group_messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.group_messages_id_seq', 25, true);


--
-- Name: messages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messages_id_seq', 66, true);


--
-- Name: socialevents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.socialevents_id_seq', 7, true);


--
-- Name: table_name_id_group_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.table_name_id_group_seq', 25, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 26, true);


--
-- Name: group_members group_members_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_pk UNIQUE (id_group, id_user);


--
-- Name: group_messages group_messages_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT group_messages_pk PRIMARY KEY (id);


--
-- Name: messages messages_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pk PRIMARY KEY (id);


--
-- Name: requests requests_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_pk PRIMARY KEY (id1, id2);


--
-- Name: socialevents_participants socialevents_participants_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents_participants
    ADD CONSTRAINT socialevents_participants_pk PRIMARY KEY (id, id_user);


--
-- Name: socialevents socialevents_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents
    ADD CONSTRAINT socialevents_pk PRIMARY KEY (id);


--
-- Name: source_destination source_destination_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.source_destination
    ADD CONSTRAINT source_destination_pk PRIMARY KEY (source_id, destination_id, message_id);


--
-- Name: groups table_name_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT table_name_pk PRIMARY KEY (id_group);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- Name: users_username_uindex; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX users_username_uindex ON public.users USING btree (username);


--
-- Name: friendships friendships_users_id1_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT friendships_users_id1_fk FOREIGN KEY (id1) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: friendships friendships_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendships
    ADD CONSTRAINT friendships_users_id_fk FOREIGN KEY (id2) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: group_members group_members_groups_id_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_groups_id_group_fk FOREIGN KEY (id_group) REFERENCES public.groups(id_group) ON DELETE CASCADE;


--
-- Name: group_members group_members_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_members
    ADD CONSTRAINT group_members_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: group_messages group_messages_group_messages_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT group_messages_group_messages_id_fk FOREIGN KEY (replied_message_id) REFERENCES public.group_messages(id) ON DELETE SET NULL;


--
-- Name: group_messages group_messages_groups_id_group_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT group_messages_groups_id_group_fk FOREIGN KEY (id_group) REFERENCES public.groups(id_group) ON DELETE CASCADE;


--
-- Name: group_messages group_messages_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.group_messages
    ADD CONSTRAINT group_messages_users_id_fk FOREIGN KEY (source_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: messages messages_messages_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_messages_id_fk FOREIGN KEY (replied_message_id) REFERENCES public.messages(id) ON DELETE SET NULL;


--
-- Name: messages messages_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_source_id_fk FOREIGN KEY (source_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: requests requests_users_id1_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_users_id1_fk FOREIGN KEY (id1) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: requests requests_users_id2_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.requests
    ADD CONSTRAINT requests_users_id2_fk FOREIGN KEY (id2) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: socialevents_participants socialevents_participants_socialevents_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents_participants
    ADD CONSTRAINT socialevents_participants_socialevents_id_fk FOREIGN KEY (id) REFERENCES public.socialevents(id) ON DELETE CASCADE;


--
-- Name: socialevents_participants socialevents_participants_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents_participants
    ADD CONSTRAINT socialevents_participants_users_id_fk FOREIGN KEY (id_user) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: socialevents socialevents_users_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.socialevents
    ADD CONSTRAINT socialevents_users_id_fk FOREIGN KEY (id_host) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: source_destination source_destination_destination_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.source_destination
    ADD CONSTRAINT source_destination_destination_id_fk FOREIGN KEY (destination_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: source_destination source_destination_messages_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.source_destination
    ADD CONSTRAINT source_destination_messages_id_fk FOREIGN KEY (message_id) REFERENCES public.messages(id) ON DELETE CASCADE;


--
-- Name: source_destination source_destination_source_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.source_destination
    ADD CONSTRAINT source_destination_source_id_fk FOREIGN KEY (source_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

