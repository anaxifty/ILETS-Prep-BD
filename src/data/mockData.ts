import {
  ReadingTest,
  ListeningTest,
  WritingTask,
  SpeakingTask,
  VideoLesson,
  MockExam,
  PartnerCenter
} from '../types';

export const SAMPLE_READING_TESTS: ReadingTest[] = [
  {
    id: "reading_test_medium_01",
    title: "The Evolution of Offshore Renewable Wind Energy",
    difficultyLevel: "Medium",
    targetBand: 6.5,
    timeLimitMinutes: 60,
    passageText: `Paragraph A
Over the past two decades, renewable energy technologies have transitioned from niche experimental concepts into mainstream drivers of global power generation. Among these, offshore wind energy has emerged as one of the most promising alternatives to fossil fuel dependence. Unlike onshore installations, which often face geographical constraints and local public opposition regarding visual noise, offshore wind farms harness the unobstructed, stronger, and more consistent winds available at sea.

Paragraph B
The engineering behind offshore wind turbines has advanced at a staggering pace. Modern turbines feature rotor diameters exceeding 200 meters and can generate up to 15 megawatts of electricity from a single unit. Crucially, innovations in floating foundation technology have expanded potential deployment areas beyond shallow coastal waters into deep oceanic zones. Floating platforms, anchored to the seabed with tension cables, allow energy producers to tap into deep-water winds that were previously inaccessible using fixed-bottom structures.

Paragraph C
Despite these technological breakthroughs, the environmental and economic impacts of offshore wind developments remain a subject of rigorous scientific study. Marine biologists emphasize that underwater noise during construction can disrupt marine mammal communication and navigation. However, post-construction ecological studies suggest that subsea turbine foundations often act as artificial reefs, fostering marine biodiversity and offering safe havens for aquatic species by restricting commercial trawling near wind farm perimeters.

Paragraph D
From an economic perspective, the levelized cost of offshore wind energy has fallen dramatically due to economies of scale and standardized manufacturing. Coastal economies in Northern Europe and East Asia are experiencing industrial revitalizations, with ports re-purposing shipbuilding yards to manufacture turbine components. As energy storage technologies mature, offshore wind is positioned to provide steady base-load power, accelerating the global transition toward zero-carbon energy grids.`,
    questions: [
      {
        id: 1,
        type: "MULTIPLE_CHOICE",
        questionText: "According to Paragraph A, what is a primary advantage of offshore wind farms over onshore installations?",
        options: [
          "A) Lower manufacturing costs of turbine blades",
          "B) Access to stronger, unobstructed, and more consistent sea winds",
          "C) Complete immunity to coastal storm damage",
          "D) Faster installation time in deep waters"
        ],
        correctAnswer: "B",
        explanation: "Paragraph A explicitly states that offshore wind farms harness unobstructed, stronger, and more consistent winds available at sea compared to onshore sites."
      },
      {
        id: 2,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Floating foundation technology enables turbine installation in deep oceanic waters.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "TRUE",
        explanation: "Paragraph B notes that floating platforms allow energy producers to tap into deep-water winds previously inaccessible with fixed-bottom structures."
      },
      {
        id: 3,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Underwater construction noise has caused permanent hearing loss in local dolphin populations.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "NOT GIVEN",
        explanation: "Paragraph C mentions noise disruption to communication and navigation, but does NOT mention permanent hearing loss."
      },
      {
        id: 4,
        type: "FILL_IN_BLANK",
        questionText: "Subsea foundations can act as artificial ______ that foster marine biodiversity.",
        correctAnswer: "reefs",
        explanation: "Paragraph C states that subsea turbine foundations often act as artificial reefs."
      },
      {
        id: 5,
        type: "FILL_IN_BLANK",
        questionText: "Manufacturing turbine components has helped revitalize coastal economies in Northern ______ and East Asia.",
        correctAnswer: "Europe",
        explanation: "Paragraph D specifically mentions Northern Europe and East Asia."
      }
    ]
  },
  {
    id: "reading_test_easy_02",
    title: "Urban Agriculture and Controlled Environment Farming",
    difficultyLevel: "Easy",
    targetBand: 5.5,
    timeLimitMinutes: 60,
    passageText: `Paragraph A
As global urban populations continue to expand rapidly, agricultural scientists are exploring innovative ways to grow fresh produce close to city centers. Urban agriculture—specifically vertical farming and hydroponic systems—offers a sustainable solution to traditional farming challenges, such as land scarcity, weather unpredictability, and long supply chain transportation emissions.

Paragraph B
Vertical farms stack crops in indoor, climate-controlled facilities using LED lighting tailored to optimize photosynthesis. Instead of soil, plants are suspended in nutrient-rich water solutions (hydroponics) or aerated mist (aeroponics). This closed-loop environment recirculates water, consuming up to 95% less water than traditional open-field agriculture while eliminating the need for synthetic chemical pesticides.

Paragraph C
While the environmental benefits are compelling, high initial capital expenditure for indoor automation, sensors, and electricity usage remains a barrier to widespread commercial adoption. Nevertheless, urban farms provide city dwellers with fresh, pesticide-free greens harvested daily, building local food resilience against climate disruptions.`,
    questions: [
      {
        id: 1,
        type: "MULTIPLE_CHOICE",
        questionText: "Which method is used in vertical farming instead of traditional soil?",
        options: [
          "A) Synthetic chemical sprays",
          "B) Nutrient-rich water solutions or aerated mist",
          "C) Volcanic ash beds",
          "D) Recycled paper pulp"
        ],
        correctAnswer: "B",
        explanation: "Paragraph B describes hydroponic nutrient-rich water solutions and aeroponic mist as soil replacements."
      },
      {
        id: 2,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Vertical farming facilities can consume up to 95% less water than traditional field farming.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "TRUE",
        explanation: "Paragraph B directly states closed-loop systems consume up to 95% less water."
      },
      {
        id: 3,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Government subsidies cover 80% of electricity costs for indoor urban farms.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "NOT GIVEN",
        explanation: "The text mentions electricity costs as a barrier, but does NOT mention government subsidies."
      },
      {
        id: 4,
        type: "FILL_IN_BLANK",
        questionText: "Vertical farms rely on climate-controlled facilities powered by LED ______.",
        correctAnswer: "lighting",
        explanation: "Paragraph B notes vertical farms use LED lighting tailored to optimize photosynthesis."
      }
    ]
  },
  {
    id: "reading_test_hard_03",
    title: "Neuroplasticity and Language Acquisition in Multilingual Adults",
    difficultyLevel: "Hard",
    targetBand: 7.5,
    timeLimitMinutes: 60,
    passageText: `Paragraph A
For decades, neuroscientists posited that second-language acquisition was bound by a strict critical period ending around puberty, after which native-like proficiency became virtually unobtainable. Recent neuroimaging advancements, however, have challenged this rigid critical period hypothesis. Functional Magnetic Resonance Imaging (fMRI) reveals that adult brains exhibit remarkable neuroplasticity, dynamically restructuring cortical neural networks during intensive language immersion.

Paragraph B
In adult multilinguals, the prefrontal cortex and left inferior parietal lobule undergo structural gray matter volumetric increases. The cognitive demands of managing two or more active linguistic systems recruit the executive control network, sharpening working memory, task-switching agility, and attentional control. This phenomenon, often referred to as the 'bilingual advantage', demonstrates that linguistic exercise strengthens general cognitive control mechanisms.

Paragraph C
Furthermore, longitudinal epidemiological studies indicate that lifelong bilingualism builds cognitive reserve—a neuroprotective buffer against age-related cognitive decline. When pathology associated with neurodegenerative disorders such as Alzheimer's develops, bilingual individuals frequently compensate better than monolinguals, delaying the clinical onset of dementia symptoms by four to five years.`,
    questions: [
      {
        id: 1,
        type: "MULTIPLE_CHOICE",
        questionText: "What has modern fMRI neuroimaging revealed regarding adult language acquisition?",
        options: [
          "A) Adult brains lose all neural adaptability after puberty",
          "B) Adult brains exhibit neuroplasticity by restructuring neural networks during intensive language study",
          "C) Language learning in adulthood shrinks gray matter density",
          "D) Monolinguals possess stronger executive control networks"
        ],
        correctAnswer: "B",
        explanation: "Paragraph A notes fMRI shows adult brains exhibit neuroplasticity and dynamically restructure cortical networks."
      },
      {
        id: 2,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Lifelong bilingualism has been shown to delay clinical dementia symptoms by 4 to 5 years.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "TRUE",
        explanation: "Paragraph C states bilingualism builds cognitive reserve, delaying clinical onset of dementia symptoms by 4 to 5 years."
      },
      {
        id: 3,
        type: "TRUE_FALSE_NOT_GIVEN",
        questionText: "Learning a third language in adulthood requires twice as much brain oxygen consumption as learning a second language.",
        options: ["TRUE", "FALSE", "NOT GIVEN"],
        correctAnswer: "NOT GIVEN",
        explanation: "The passage does NOT mention brain oxygen consumption ratios for third language learning."
      },
      {
        id: 4,
        type: "FILL_IN_BLANK",
        questionText: "Lifelong bilingualism builds cognitive ______ which serves as a buffer against cognitive decline.",
        correctAnswer: "reserve",
        explanation: "Paragraph C specifically uses the phrase cognitive reserve."
      }
    ]
  }
];

export const SAMPLE_LISTENING_TESTS: ListeningTest[] = [
  {
    id: "listening_test_medium_01",
    title: "IELTS Listening Section 1 & 2: Student Accommodation & Campus Library",
    difficultyLevel: "Medium",
    targetBand: 6.5,
    audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
    audioDurationSeconds: 180,
    transferTimeSeconds: 600,
    questions: [
      {
        id: 1,
        sectionNumber: 1,
        type: "FORM_COMPLETION",
        questionText: "Complete Question 1 on Room Preference in the Housing Form.",
        formContext: "Student Name: Sarah Jenkins\nTarget Move-in Date: 15th September\nRoom Preference: [1]\nContact Extension: [2]",
        correctAnswer: "En-suite",
        explanation: "The student requests an en-suite single room option."
      },
      {
        id: 2,
        sectionNumber: 1,
        type: "FORM_COMPLETION",
        questionText: "Complete Question 2 on Contact Extension.",
        formContext: "Contact Extension: 07700 [2]",
        correctAnswer: "900142",
        explanation: "The housing officer specifies contact extension 900142."
      },
      {
        id: 3,
        sectionNumber: 2,
        type: "MULTIPLE_CHOICE",
        questionText: "What time does the campus central library open on weekend mornings?",
        options: ["A) 08:00 AM", "B) 09:30 AM", "C) 10:00 AM", "D) 12:00 PM"],
        correctAnswer: "B",
        explanation: "The library orientation states weekend opening hours are 09:30 AM."
      },
      {
        id: 4,
        sectionNumber: 2,
        type: "MATCHING",
        questionText: "Match the Group Study Rooms (Item 1) to their floor location in the library.",
        matchingOptions: ["A - Ground Floor", "B - 2nd Floor East Wing", "C - 3rd Floor Basement"],
        correctAnswer: "B",
        explanation: "Group Study Rooms are located on the 2nd Floor East Wing."
      },
      {
        id: 5,
        sectionNumber: 2,
        type: "MATCHING",
        questionText: "Match the Multimedia Lab (Item 2) to its floor location in the library.",
        matchingOptions: ["A - Ground Floor", "B - 2nd Floor East Wing", "C - 3rd Floor Basement"],
        correctAnswer: "A",
        explanation: "The audio specifies the Multimedia Lab is on the Ground Floor."
      }
    ]
  },
  {
    id: "listening_test_easy_02",
    title: "IELTS Listening Section 1: Community Center Sports Registration",
    difficultyLevel: "Easy",
    targetBand: 5.5,
    audioUrl: "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
    audioDurationSeconds: 150,
    transferTimeSeconds: 600,
    questions: [
      {
        id: 1,
        sectionNumber: 1,
        type: "FORM_COMPLETION",
        questionText: "Complete Question 1 on Membership Tier.",
        formContext: "Membership Tier: [1]\nMonthly Fee: $45\nSport: Badminton",
        correctAnswer: "Gold Pass",
        explanation: "The caller selects the Gold Pass membership level."
      },
      {
        id: 2,
        sectionNumber: 1,
        type: "MULTIPLE_CHOICE",
        questionText: "How often are beginner swimming classes held?",
        options: ["A) Once a week", "B) Twice a week", "C) Every weekend"],
        correctAnswer: "B",
        explanation: "Classes are held twice a week on Tuesdays and Thursdays."
      },
      {
        id: 3,
        sectionNumber: 1,
        type: "MATCHING",
        questionText: "Match the Fitness Gym operating hours.",
        matchingOptions: ["A - 06:00 to 22:00", "B - 08:00 to 20:00", "C - 24 Hours"],
        correctAnswer: "A",
        explanation: "The gym operates daily from 06:00 to 22:00."
      }
    ]
  }
];

export const SAMPLE_WRITING_TASKS: WritingTask[] = [
  {
    id: "writing_task_01",
    taskType: "Task 1",
    title: "Global Renewable Energy Production Trends (2010–2025)",
    prompt: "The line chart illustrates the percentage of electricity generated from renewable sources in Europe, Asia-Pacific, North America, and Latin America from 2010 to 2025. Summarise the information by selecting and reporting the main features, and make comparisons where relevant.",
    imageUrl: "https://images.unsplash.com/photo-1466611653911-95081537e5b7?auto=format&fit=crop&w=800&q=80",
    difficultyLevel: "Medium",
    targetWordCount: 150,
    recommendedTimeMinutes: 20
  },
  {
    id: "writing_task_02",
    taskType: "Task 2",
    title: "Artificial Intelligence & Workplace Automation",
    prompt: "Some people believe that the increasing reliance on artificial intelligence and automation in the workplace will lead to widespread unemployment, while others argue it will create new opportunities and higher quality jobs. Discuss both views and give your own opinion.",
    imageUrl: null,
    difficultyLevel: "Medium",
    targetWordCount: 250,
    recommendedTimeMinutes: 40
  },
  {
    id: "writing_task_03",
    taskType: "Task 2",
    title: "Environmental Protection vs Economic Growth",
    prompt: "Economic development often results in environmental degradation. Some people think that governments should prioritize environmental protection over economic growth, while others believe that economic prosperity is essential for solving environmental issues. Discuss both sides and give your opinion.",
    imageUrl: null,
    difficultyLevel: "Hard",
    targetWordCount: 250,
    recommendedTimeMinutes: 40
  }
];

export const SAMPLE_SPEAKING_TASKS: SpeakingTask[] = [
  {
    id: "speaking_task_01",
    partNumber: 1,
    title: "Part 1: Hometown, Studies & Free Time",
    topic: "Personal & Daily Life Routine",
    part1Questions: [
      "Let's talk about your hometown. Where is your hometown located?",
      "Do you work or are you a student? What do you find most interesting about it?",
      "What do you usually do in your free time to relax after a long day?"
    ],
    part2CueCard: "",
    part2Bullets: [],
    part3Questions: [],
    difficultyLevel: "Easy"
  },
  {
    id: "speaking_task_02",
    partNumber: 2,
    title: "Part 2: Cue Card - Memorable Journey",
    topic: "Travel & Memorable Experiences",
    part1Questions: [],
    part2CueCard: "Describe a memorable trip or journey you took that made a lasting impression on you.",
    part2Bullets: [
      "Where you went and who you traveled with",
      "What activities you did during the trip",
      "Why this journey was particularly memorable to you"
    ],
    part3Questions: [
      "How have people's travel habits changed in your country over the last decade?",
      "Do you think international tourism does more harm or good to local cultures?"
    ],
    difficultyLevel: "Medium"
  },
  {
    id: "speaking_task_03",
    partNumber: 3,
    title: "Part 3: Deep Discussion - AI & Future Society",
    topic: "Technology, Automation & Future Employment",
    part1Questions: [],
    part2CueCard: "",
    part2Bullets: [],
    part3Questions: [
      "In what ways might artificial intelligence impact traditional employment structures in the next 20 years?",
      "Do you agree that ethical guidelines should regulate AI development globally?",
      "How can educational institutions adapt curricula to prepare youth for an automated workplace?"
    ],
    difficultyLevel: "Hard"
  }
];

export const SAMPLE_VIDEO_LESSONS: VideoLesson[] = [
  {
    id: "lesson_l1",
    title: "Mastering Section 1 Form Completion",
    skillCategory: "Listening",
    topic: "Form & Table Completion",
    description: "Avoid the spelling and number mistakes students make most in Section 1.",
    duration: "08:15",
    thumbnailUrl: "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
  },
  {
    id: "lesson_l2",
    title: "Map & Layout Labelling Walkthrough",
    skillCategory: "Listening",
    topic: "Map & Diagram Matching",
    description: "The direction words you need for map and diagram questions.",
    duration: "10:30",
    thumbnailUrl: "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
  },
  {
    id: "lesson_r1",
    title: "Skimming & Scanning Academic Passages",
    skillCategory: "Reading",
    topic: "Time Management & Key Words",
    description: "How to get through all 3 passages in 60 minutes with maximum accuracy.",
    duration: "12:00",
    thumbnailUrl: "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
  },
  {
    id: "lesson_r2",
    title: "True / False / Not Given Logic Demystified",
    skillCategory: "Reading",
    topic: "Factual & Claim Matching",
    description: "Disambiguate 'Not Given' claims from contradicted facts using text logic and keyword mapping techniques.",
    duration: "09:45",
    thumbnailUrl: "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoylines.mp4"
  },
  {
    id: "lesson_w1",
    title: "Task 1 Overview & Trend Analysis Formulations",
    skillCategory: "Writing",
    topic: "Academic Task 1 Line & Bar Charts",
    description: "How to write a Band 8+ overall summary sentence and compare main data trends with high-scoring grammatical accuracy.",
    duration: "14:20",
    thumbnailUrl: "https://images.unsplash.com/photo-1460925895917-afdab827c52f?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4"
  },
  {
    id: "lesson_s1",
    title: "Part 2 Cue Card 2-Minute Storytelling Flow",
    skillCategory: "Speaking",
    topic: "Cue Card Delivery & Coherence",
    description: "Step-by-step cue card preparation method to structure your 1-minute notes and maintain natural speech for 2 full minutes.",
    duration: "11:10",
    thumbnailUrl: "https://images.unsplash.com/photo-1475721027785-f74eccf877e2?auto=format&fit=crop&w=600&q=80",
    videoUrl: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4"
  }
];

export const SAMPLE_MOCK_EXAMS: MockExam[] = [
  {
    id: "mock_weekly_01",
    title: "Weekly Mock Exam #1 (Standard)",
    examType: "WEEKLY",
    difficultyLevel: "Standard",
    startDate: "2026-07-28",
    endDate: "2026-08-04",
    listeningTestId: "listening_test_medium_01",
    readingTestId: "reading_test_medium_01",
    writingTaskId: "writing_task_02",
    speakingTaskId: "speaking_task_01",
    totalDurationMinutes: 160
  },
  {
    id: "mock_monthly_01",
    title: "Monthly Full Sitting Exam (Challenging)",
    examType: "MONTHLY",
    difficultyLevel: "Challenging",
    startDate: "2026-07-01",
    endDate: "2026-07-31",
    listeningTestId: "listening_test_easy_02",
    readingTestId: "reading_test_hard_03",
    writingTaskId: "writing_task_03",
    speakingTaskId: "speaking_task_02",
    totalDurationMinutes: 160
  }
];

export const SAMPLE_PARTNER_CENTERS: PartnerCenter[] = [
  {
    id: "center_dhanmondi_01",
    name: "Dhanmondi Practice Academy & IELTS Lab",
    address: "4th Floor, House 12, Road 5, Dhanmondi, Dhaka",
    city: "Dhaka",
    latitude: 23.7461,
    longitude: 90.3742,
    description: "State-of-the-art practice facility with high-fidelity headphone stations for practice listening tests and dedicated exam desks.",
    contactPhone: "+880 1711-223344",
    contactEmail: "dhanmondi@partnerpractice.bd",
    rating: 4.9,
    slots: [
      {
        slotId: "slot_dhaka_01",
        date: "2026-08-05",
        time: "10:00 AM - 01:00 PM",
        title: "Full Practice Mock Test (4 Skills)",
        slotType: "PRACTICE_MOCK",
        priceBdt: 1500,
        capacity: 20,
        seatsRemaining: 8
      },
      {
        slotId: "slot_dhaka_02",
        date: "2026-08-05",
        time: "03:00 PM - 05:00 PM",
        title: "Writing Task 1 & 2 Feedback Session",
        slotType: "COACHING_SESSION",
        priceBdt: 800,
        capacity: 15,
        seatsRemaining: 12
      },
      {
        slotId: "slot_dhaka_03",
        date: "2026-08-08",
        time: "10:00 AM - 01:00 PM",
        title: "Weekly Full Practice Mock Sitting",
        slotType: "PRACTICE_MOCK",
        priceBdt: 1500,
        capacity: 20,
        seatsRemaining: 15
      }
    ]
  },
  {
    id: "center_uttara_02",
    name: "Uttara IELTS Practice & Prep Center",
    address: "Level 5, Sector 3, Uttara Model Town, Dhaka",
    city: "Dhaka",
    latitude: 23.8759,
    longitude: 90.3795,
    description: "Quiet, sound-proof practice rooms and expert evaluation coaches for mock speaking interviews and writing review.",
    contactPhone: "+880 1812-334455",
    contactEmail: "uttara@partnerprep.bd",
    rating: 4.8,
    slots: [
      {
        slotId: "slot_uttara_01",
        date: "2026-08-06",
        time: "09:30 AM - 12:30 PM",
        title: "Full Practice Mock Test (4 Skills)",
        slotType: "PRACTICE_MOCK",
        priceBdt: 1400,
        capacity: 25,
        seatsRemaining: 10
      },
      {
        slotId: "slot_uttara_02",
        date: "2026-08-07",
        time: "02:30 PM - 04:30 PM",
        title: "1-on-1 Speaking Mock Interview",
        slotType: "COACHING_SESSION",
        priceBdt: 1000,
        capacity: 10,
        seatsRemaining: 5
      }
    ]
  },
  {
    id: "center_agrabad_03",
    name: "Agrabad Practice Hub & Coaching",
    address: "Jahan Building, Agrabad Commercial Area, Chittagong",
    city: "Chittagong",
    latitude: 22.3244,
    longitude: 91.8143,
    description: "Premier Chittagong center offering timed full mock exam sittings with instant score analytics.",
    contactPhone: "+880 1913-445566",
    contactEmail: "ctg.agrabad@partnerprep.bd",
    rating: 4.7,
    slots: [
      {
        slotId: "slot_ctg_01",
        date: "2026-08-06",
        time: "10:00 AM - 01:00 PM",
        title: "Chittagong Full Practice Mock Sitting",
        slotType: "PRACTICE_MOCK",
        priceBdt: 1300,
        capacity: 20,
        seatsRemaining: 12
      },
      {
        slotId: "slot_ctg_02",
        date: "2026-08-09",
        time: "04:00 PM - 06:00 PM",
        title: "Listening & Reading Strategy Workshop",
        slotType: "COACHING_SESSION",
        priceBdt: 750,
        capacity: 20,
        seatsRemaining: 18
      }
    ]
  },
  {
    id: "center_sylhet_04",
    name: "Sylhet Zindabazar Practice Center",
    address: "3rd Floor, Millennium Market, Zindabazar, Sylhet",
    city: "Sylhet",
    latitude: 24.8949,
    longitude: 91.8687,
    description: "Spacious study hall with real exam condition environment for practice mock tests.",
    contactPhone: "+880 1614-556677",
    contactEmail: "sylhet@partnerprep.bd",
    rating: 4.8,
    slots: [
      {
        slotId: "slot_sylhet_01",
        date: "2026-08-07",
        time: "10:00 AM - 01:00 PM",
        title: "Sylhet Full Practice Mock Test",
        slotType: "PRACTICE_MOCK",
        priceBdt: 1350,
        capacity: 20,
        seatsRemaining: 14
      }
    ]
  }
];
