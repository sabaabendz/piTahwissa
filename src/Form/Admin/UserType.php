<?php

namespace App\Form\Admin;

use App\Entity\Role;
use App\Entity\User;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\CheckboxType;
use Symfony\Component\Form\Extension\Core\Type\EmailType;
use Symfony\Component\Form\Extension\Core\Type\NumberType;
use Symfony\Component\Form\Extension\Core\Type\PasswordType;
use Symfony\Component\Form\Extension\Core\Type\RepeatedType;
use Symfony\Component\Form\Extension\Core\Type\TextareaType;
use Symfony\Component\Form\Extension\Core\Type\TextType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;
use Symfony\Component\Validator\Constraints\Length;
use Symfony\Component\Validator\Constraints\NotBlank;

class UserType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $passwordConstraints = [
            new Length(min: 8, max: 4096, minMessage: 'Password must be at least {{ limit }} characters long.'),
        ];

        if ($options['require_password']) {
            array_unshift($passwordConstraints, new NotBlank(message: 'Please enter a password.'));
        }

        $builder
            ->add('email', EmailType::class, [
                'label' => 'Email',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('plainPassword', RepeatedType::class, [
                'type' => PasswordType::class,
                'mapped' => false,
                'required' => $options['require_password'],
                'invalid_message' => 'Passwords do not match.',
                'constraints' => $passwordConstraints,
                'first_options' => [
                    'label' => 'Password',
                    'attr' => [
                        'class' => 'form-control',
                        'autocomplete' => 'new-password',
                    ],
                    'label_attr' => ['class' => 'form-label'],
                ],
                'second_options' => [
                    'label' => 'Confirm password',
                    'attr' => [
                        'class' => 'form-control',
                        'autocomplete' => 'new-password',
                    ],
                    'label_attr' => ['class' => 'form-label'],
                ],
            ])
            ->add('firstName', TextType::class, [
                'required' => false,
                'label' => 'First name',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('lastName', TextType::class, [
                'required' => false,
                'label' => 'Last name',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('phone', TextType::class, [
                'required' => false,
                'label' => 'Phone',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('description', TextareaType::class, [
                'required' => false,
                'label' => 'Description',
                'attr' => ['class' => 'form-control', 'rows' => 3],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('address', TextType::class, [
                'required' => false,
                'label' => 'Address',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('city', TextType::class, [
                'required' => false,
                'label' => 'City',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('country', TextType::class, [
                'required' => false,
                'label' => 'Country',
                'attr' => ['class' => 'form-control'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('latitude', NumberType::class, [
                'required' => false,
                'label' => 'Latitude',
                'scale' => 8,
                'html5' => true,
                'attr' => ['class' => 'form-control', 'step' => 'any'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('longitude', NumberType::class, [
                'required' => false,
                'label' => 'Longitude',
                'scale' => 8,
                'html5' => true,
                'attr' => ['class' => 'form-control', 'step' => 'any'],
                'label_attr' => ['class' => 'form-label'],
            ])
            ->add('isVerified', CheckboxType::class, [
                'required' => false,
                'label' => 'Verified',
                'attr' => ['class' => 'form-check-input'],
                'label_attr' => ['class' => 'form-check-label'],
            ])
            ->add('isActive', CheckboxType::class, [
                'required' => false,
                'label' => 'Active',
                'attr' => ['class' => 'form-check-input'],
                'label_attr' => ['class' => 'form-check-label'],
            ])
            ->add('role', EntityType::class, [
                'class' => Role::class,
                'choice_label' => 'name',
                'label' => 'Role',
                'placeholder' => 'Select a role',
                'attr' => ['class' => 'form-select'],
                'label_attr' => ['class' => 'form-label'],
            ]);
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => User::class,
            'require_password' => false,
        ]);

        $resolver->setAllowedTypes('require_password', 'bool');
    }
}
